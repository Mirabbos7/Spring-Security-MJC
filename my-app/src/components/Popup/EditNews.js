import React, { useState, useEffect, useCallback } from 'react';
import { getAuthToken } from '../../utils/authUtils';
import './EditNews.css';

const EditNews = ({ news, onEdit, onClose }) => {
    const [title, setTitle] = useState(news.title || '');
    const [content, setContent] = useState(news.content || '');
    const [authorId, setAuthorId] = useState('');
    const [selectedTags, setSelectedTags] = useState([]);
    const [authors, setAuthors] = useState([]);
    const [tags, setTags] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    const fetchAuthorsAndTags = useCallback(async () => {
        const token = getAuthToken();

        try {
            setLoading(true);

            // Fetch authors
            const authorsResponse = await fetch('http://localhost:8082/api/v1/author/readAll?page=0&size=1000', {
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });

            // Fetch tags
            const tagsResponse = await fetch('http://localhost:8082/api/v1/tag?page=0&size=1000', {
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });

            if (authorsResponse.ok && tagsResponse.ok) {
                const authorsData = await authorsResponse.json();
                const tagsData = await tagsResponse.json();

                setAuthors(authorsData);
                setTags(tagsData);

                // Set current author
                const currentAuthor = authorsData.find(a => a.name === news.authorName);
                if (currentAuthor) {
                    setAuthorId(currentAuthor.id);
                }

                // Set current tags
                if (news.tagNames && news.tagNames.length > 0) {
                    const currentTagIds = tagsData
                        .filter(tag => news.tagNames.includes(tag.name))
                        .map(tag => tag.id);
                    setSelectedTags(currentTagIds);
                }
            } else {
                setError('Failed to load authors and tags');
            }
        } catch (err) {
            console.error('Error fetching data:', err);
            setError('Failed to load data');
        } finally {
            setLoading(false);
        }
    }, [news.authorName, news.tagNames]);

    useEffect(() => {
        fetchAuthorsAndTags();
    }, [fetchAuthorsAndTags]);

    const handleTagToggle = (tagId) => {
        setSelectedTags(prev =>
            prev.includes(tagId)
                ? prev.filter(id => id !== tagId)
                : [...prev, tagId]
        );
    };

    const handleSave = async () => {
        if (!title.trim()) {
            alert('Please enter a title');
            return;
        }

        if (!content.trim()) {
            alert('Please enter content');
            return;
        }

        if (!authorId) {
            alert('Please select an author');
            return;
        }

        if (selectedTags.length === 0) {
            alert('Please select at least one tag');
            return;
        }

        const token = getAuthToken();
        console.log('Auth token:', token ? 'Found' : 'Not found');

        // Get author name from selected author ID
        const selectedAuthor = authors.find(a => a.id === parseInt(authorId));
        const authorName = selectedAuthor ? selectedAuthor.name : '';

        // Get tag names from selected tag IDs
        const tagNames = tags
            .filter(tag => selectedTags.includes(tag.id))
            .map(tag => tag.name);

        // Build request body to match backend expectations (authorName and tagNames, NOT IDs)
        const newsData = {
            title: title.trim(),
            content: content.trim(),
            authorName: authorName,
            tagNames: tagNames
        };

        console.log('Updating news ID:', news.id);
        console.log('Request payload:', JSON.stringify(newsData, null, 2));

        try {
            const response = await fetch(`http://localhost:8082/api/v1/news/${news.id}`, {
                method: 'PATCH',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(newsData)
            });

            console.log('Response status:', response.status);
            console.log('Response headers:', Object.fromEntries(response.headers.entries()));

            if (response.ok || response.status === 204) {
                console.log('Update successful, fetching updated news...');

                // If successful, fetch the updated news to get latest data
                const updatedNewsResponse = await fetch(`http://localhost:8082/api/v1/news/${news.id}`, {
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json'
                    }
                });

                if (updatedNewsResponse.ok) {
                    const updatedNews = await updatedNewsResponse.json();
                    console.log('Updated news received:', updatedNews);
                    onEdit(updatedNews);
                } else {
                    console.warn('Could not fetch updated news, using local data');
                    // If can't fetch updated news, just pass what we have
                    onEdit({ ...news, title, content });
                }
            } else {
                const errorText = await response.text();
                console.error('Update failed with status:', response.status);
                console.error('Error response:', errorText);
                alert('Failed to update news. Check console for details.\nError: ' + errorText);
            }
        } catch (error) {
            console.error('Exception during update:', error);
            alert('An error occurred: ' + error.message);
        }
    };

    if (loading) {
        return (
            <div className="edit-modal-overlay">
                <div className="edit-modal-content">
                    <div className="edit-modal-loading">Loading...</div>
                </div>
            </div>
        );
    }

    return (
        <div className="edit-modal-overlay" onClick={onClose}>
            <div className="edit-modal-content" onClick={(e) => e.stopPropagation()}>
                <div className="edit-modal-header">
                    <h2 className="edit-modal-title">Edit News</h2>
                    <button className="edit-modal-close" onClick={onClose}>
                        ✕
                    </button>
                </div>

                {error && <div className="edit-modal-error">{error}</div>}

                <div className="edit-modal-body">
                    <div className="edit-form-group">
                        <label className="edit-form-label">TITLE</label>
                        <input
                            type="text"
                            className="edit-form-input"
                            value={title}
                            onChange={(e) => setTitle(e.target.value)}
                            placeholder="Enter news title"
                        />
                    </div>

                    <div className="edit-form-group">
                        <label className="edit-form-label">CONTENT</label>
                        <textarea
                            className="edit-form-textarea"
                            value={content}
                            onChange={(e) => setContent(e.target.value)}
                            placeholder="Enter news content"
                            rows={5}
                        />
                    </div>

                    <div className="edit-form-group">
                        <label className="edit-form-label">AUTHOR</label>
                        <select
                            className="edit-form-select"
                            value={authorId}
                            onChange={(e) => setAuthorId(e.target.value)}
                        >
                            <option value="">Select an author</option>
                            {authors.map(author => (
                                <option key={author.id} value={author.id}>
                                    {author.name}
                                </option>
                            ))}
                        </select>
                    </div>

                    <div className="edit-form-group">
                        <label className="edit-form-label">TAGS (Optional)</label>
                        <div className="edit-tags-grid">
                            {tags.length > 0 ? (
                                tags.map(tag => (
                                    <label key={tag.id} className="edit-tag-checkbox">
                                        <input
                                            type="checkbox"
                                            checked={selectedTags.includes(tag.id)}
                                            onChange={() => handleTagToggle(tag.id)}
                                        />
                                        <span className="edit-tag-label">{tag.name}</span>
                                    </label>
                                ))
                            ) : (
                                <p className="edit-no-tags">No tags available</p>
                            )}
                        </div>
                    </div>
                </div>

                <div className="edit-modal-footer">
                    <button className="edit-modal-cancel" onClick={onClose}>
                        Cancel
                    </button>
                    <button className="edit-modal-save" onClick={handleSave}>
                        Save
                    </button>
                </div>
            </div>
        </div>
    );
};

export default EditNews;