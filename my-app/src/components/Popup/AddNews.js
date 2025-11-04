import React, { useState, useEffect } from "react";

const AddNews = ({ onAdd }) => {
    const [showModal, setShowModal] = useState(false);
    const [title, setTitle] = useState("");
    const [content, setContent] = useState("");
    const [tags, setTags] = useState([]);
    const [tagInput, setTagInput] = useState("");
    const [authorName, setAuthorName] = useState("");
    const [loading, setLoading] = useState(false);

    // Debug: Check localStorage when component mounts
    useEffect(() => {
        console.log('AddNews component mounted');
        console.log('All localStorage keys:', Object.keys(localStorage));
        console.log('localStorage.auth:', localStorage.getItem('auth'));
        console.log('localStorage.token:', localStorage.getItem('token'));
    }, []);

    const handleSave = async () => {
        if (!title.trim() || !content.trim()) {
            alert("Please fill in title and content");
            return;
        }

        if (!authorName.trim()) {
            alert("Please fill in author name");
            return;
        }

        // Debug: Log all localStorage content
        console.log('=== TOKEN SEARCH START ===');
        console.log('All localStorage:', { ...localStorage });
        console.log('localStorage length:', localStorage.length);

        for (let i = 0; i < localStorage.length; i++) {
            const key = localStorage.key(i);
            console.log(`localStorage[${key}]:`, localStorage.getItem(key));
        }

        // Get token from localStorage
        let token = null;

        // Try getting from 'auth' key
        const authData = localStorage.getItem('auth');
        console.log('Raw auth data:', authData);

        if (authData) {
            try {
                const parsed = JSON.parse(authData);
                token = parsed.token;
                console.log('Token extracted from auth:', token);
            } catch (e) {
                console.error('Failed to parse auth data:', e);
            }
        }

        // If still no token, try 'token' key directly
        if (!token) {
            token = localStorage.getItem('token');
            console.log('Token from direct storage:', token);
        }

        // Try other possible keys
        if (!token) {
            const userAuth = localStorage.getItem('userAuth');
            const user = localStorage.getItem('user');
            const authToken = localStorage.getItem('authToken');

            console.log('Trying alternative keys...');
            console.log('userAuth:', userAuth);
            console.log('user:', user);
            console.log('authToken:', authToken);

            if (userAuth) {
                try {
                    token = JSON.parse(userAuth).token;
                } catch (e) {}
            }
            if (!token && user) {
                try {
                    token = JSON.parse(user).token;
                } catch (e) {}
            }
            if (!token && authToken) {
                token = authToken;
            }
        }

        console.log('Final token:', token);
        console.log('=== TOKEN SEARCH END ===');

        if (!token) {
            console.error('No token found in localStorage');
            console.log('Please check your auth.js - localStorage might not be saving correctly');
            alert("You are not authenticated. Please login first.");
            return;
        }

        // Create request body matching NewsDtoRequest format
        const newNewsRequest = {
            title: title.trim(),
            content: content.trim(),
            authorName: authorName.trim(),
            tagNames: tags.length > 0 ? tags : null
        };

        console.log('Sending request with token:', token.substring(0, 20) + '...');
        console.log('Request body:', newNewsRequest);

        setLoading(true);

        try {
            // Make API request with Authorization header
            const response = await fetch('http://localhost:8082/api/v1/news', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(newNewsRequest),
            });

            console.log('Response status:', response.status);

            if (!response.ok) {
                if (response.status === 401) {
                    throw new Error('Unauthorized. Please login again.');
                }
                const errorText = await response.text();
                console.error('Error response:', errorText);
                throw new Error('Failed to save news: ' + errorText);
            }

            const savedNews = await response.json();
            console.log('News saved successfully:', savedNews);

            // Call parent callback if provided
            if (onAdd) onAdd(savedNews);

            // Reset form
            setTitle("");
            setContent("");
            setTags([]);
            setTagInput("");
            setAuthorName("");
            setShowModal(false);

            alert("News added successfully!");
        } catch (error) {
            console.error('Error saving news:', error);
            alert(error.message || "Failed to save news. Please try again.");
        } finally {
            setLoading(false);
        }
    };

    const handleAddTag = () => {
        if (tagInput.trim() && !tags.includes(tagInput.trim().toUpperCase())) {
            setTags([...tags, tagInput.trim().toUpperCase()]);
            setTagInput("");
        }
    };

    const handleRemoveTag = (tagToRemove) => {
        setTags(tags.filter(tag => tag !== tagToRemove));
    };

    const handleCancel = () => {
        setTitle("");
        setContent("");
        setTags([]);
        setTagInput("");
        setAuthorName("");
        setShowModal(false);
    };

    return (
        <div>
            <button onClick={() => setShowModal(true)}>Add News</button>

            {showModal && (
                <div style={{
                    position: 'fixed',
                    top: 0,
                    left: 0,
                    right: 0,
                    bottom: 0,
                    backgroundColor: 'rgba(0, 0, 0, 0.5)',
                    display: 'flex',
                    justifyContent: 'center',
                    alignItems: 'center',
                    zIndex: 1000
                }}>
                    <div style={{
                        backgroundColor: 'white',
                        padding: '40px',
                        borderRadius: '8px',
                        width: '500px',
                        maxWidth: '90%',
                        position: 'relative',
                        maxHeight: '90vh',
                        overflowY: 'auto'
                    }}>
                        <button
                            onClick={handleCancel}
                            style={{
                                position: 'absolute',
                                right: '20px',
                                top: '20px',
                                border: 'none',
                                background: 'none',
                                fontSize: '24px',
                                cursor: 'pointer'
                            }}
                            disabled={loading}
                        >
                            ×
                        </button>

                        <h2 style={{ marginTop: 0, marginBottom: '30px' }}>New news</h2>

                        <div style={{ marginBottom: '20px' }}>
                            <label style={{
                                display: 'block',
                                marginBottom: '8px',
                                color: '#999',
                                fontSize: '12px',
                                textTransform: 'uppercase',
                                letterSpacing: '1px'
                            }}>
                                TITLE
                            </label>
                            <input
                                type="text"
                                value={title}
                                onChange={(e) => setTitle(e.target.value)}
                                disabled={loading}
                                style={{
                                    width: '100%',
                                    padding: '10px',
                                    border: '1px solid #ddd',
                                    borderRadius: '4px',
                                    fontSize: '14px',
                                    boxSizing: 'border-box'
                                }}
                            />
                        </div>

                        <div style={{ marginBottom: '20px' }}>
                            <label style={{
                                display: 'block',
                                marginBottom: '8px',
                                color: '#999',
                                fontSize: '12px',
                                textTransform: 'uppercase',
                                letterSpacing: '1px'
                            }}>
                                CONTENT
                            </label>
                            <textarea
                                value={content}
                                onChange={(e) => setContent(e.target.value)}
                                rows="6"
                                disabled={loading}
                                style={{
                                    width: '100%',
                                    padding: '10px',
                                    border: '1px solid #ddd',
                                    borderRadius: '4px',
                                    fontSize: '14px',
                                    resize: 'vertical',
                                    boxSizing: 'border-box'
                                }}
                            />
                        </div>

                        <div style={{ marginBottom: '20px' }}>
                            <label style={{
                                display: 'block',
                                marginBottom: '8px',
                                color: '#999',
                                fontSize: '12px',
                                textTransform: 'uppercase',
                                letterSpacing: '1px'
                            }}>
                                AUTHOR
                            </label>
                            <input
                                type="text"
                                value={authorName}
                                onChange={(e) => setAuthorName(e.target.value)}
                                disabled={loading}
                                placeholder="Name of the author"
                                style={{
                                    width: '100%',
                                    padding: '10px',
                                    border: '1px solid #ddd',
                                    borderRadius: '4px',
                                    fontSize: '14px',
                                    boxSizing: 'border-box'
                                }}
                            />
                        </div>

                        <div style={{ marginBottom: '30px' }}>
                            <label style={{
                                display: 'block',
                                marginBottom: '8px',
                                color: '#999',
                                fontSize: '12px',
                                textTransform: 'uppercase',
                                letterSpacing: '1px'
                            }}>
                                TAGS
                            </label>

                            <div style={{ display: 'flex', flexWrap: 'wrap', gap: '8px', marginBottom: '10px' }}>
                                {tags.map((tag, index) => (
                                    <span
                                        key={index}
                                        style={{
                                            backgroundColor: '#e3f2fd',
                                            color: '#2196F3',
                                            padding: '6px 12px',
                                            borderRadius: '16px',
                                            fontSize: '12px',
                                            display: 'inline-flex',
                                            alignItems: 'center',
                                            gap: '6px'
                                        }}
                                    >
                                        {tag}
                                        <button
                                            onClick={() => handleRemoveTag(tag)}
                                            disabled={loading}
                                            style={{
                                                border: 'none',
                                                background: 'none',
                                                cursor: 'pointer',
                                                padding: 0,
                                                fontSize: '14px',
                                                color: '#2196F3'
                                            }}
                                        >
                                            ×
                                        </button>
                                    </span>
                                ))}
                            </div>

                            <div style={{ display: 'flex', gap: '8px' }}>
                                <input
                                    type="text"
                                    value={tagInput}
                                    onChange={(e) => setTagInput(e.target.value)}
                                    onKeyPress={(e) => {
                                        if (e.key === 'Enter') {
                                            e.preventDefault();
                                            handleAddTag();
                                        }
                                    }}
                                    placeholder="Add tags"
                                    disabled={loading}
                                    style={{
                                        flex: 1,
                                        padding: '10px',
                                        border: '1px solid #ddd',
                                        borderRadius: '20px',
                                        fontSize: '14px'
                                    }}
                                />
                                <button
                                    onClick={handleAddTag}
                                    disabled={loading}
                                    style={{
                                        padding: '10px 20px',
                                        border: '1px solid #ddd',
                                        borderRadius: '20px',
                                        background: 'white',
                                        cursor: 'pointer',
                                        fontSize: '18px'
                                    }}
                                >
                                    +
                                </button>
                            </div>
                        </div>

                        <div style={{
                            display: 'flex',
                            justifyContent: 'flex-end',
                            gap: '12px'
                        }}>
                            <button
                                onClick={handleCancel}
                                disabled={loading}
                                style={{
                                    padding: '10px 24px',
                                    border: '1px solid #ddd',
                                    borderRadius: '20px',
                                    background: 'white',
                                    cursor: loading ? 'not-allowed' : 'pointer',
                                    fontSize: '14px',
                                    opacity: loading ? 0.6 : 1
                                }}
                            >
                                Cancel
                            </button>
                            <button
                                onClick={handleSave}
                                disabled={loading}
                                style={{
                                    padding: '10px 24px',
                                    border: 'none',
                                    borderRadius: '20px',
                                    background: '#2196F3',
                                    color: 'white',
                                    cursor: loading ? 'not-allowed' : 'pointer',
                                    fontSize: '14px',
                                    opacity: loading ? 0.6 : 1
                                }}
                            >
                                {loading ? 'Saving...' : 'Save'}
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default AddNews;