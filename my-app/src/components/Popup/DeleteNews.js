import React from 'react';
import './DeleteNews.css';

const DeleteNews = ({ news, onDelete, onClose }) => {
    const getAuthToken = () => {
        const userData = localStorage.getItem('user');
        if (!userData) return null;
        try {
            const parsed = JSON.parse(userData);
            return parsed.token || parsed.accessToken;
        } catch {
            return null;
        }
    };

    const handleDelete = async () => {
        const token = getAuthToken();

        try {
            const response = await fetch(`http://localhost:8082/api/v1/news/${news.id}`, {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });

            if (response.ok || response.status === 204) {
                onDelete(news.id);
            } else {
                console.error('Failed to delete news');
                alert('Failed to delete news. Please try again.');
            }
        } catch (error) {
            console.error('Error deleting news:', error);
            alert('An error occurred while deleting the news.');
        }
    };

    return (
        <div className="delete-modal-overlay" onClick={onClose}>
            <div className="delete-modal-content" onClick={(e) => e.stopPropagation()}>
                <button className="delete-modal-close" onClick={onClose}>
                    ✕
                </button>

                <div className="delete-modal-icon">
                    🗑️
                </div>

                <h2 className="delete-modal-title">
                    Do you really want to delete this news?
                </h2>

                <div className="delete-modal-actions">
                    <button className="delete-modal-cancel" onClick={onClose}>
                        Cancel
                    </button>
                    <button className="delete-modal-delete" onClick={handleDelete}>
                        Delete
                    </button>
                </div>
            </div>
        </div>
    );
};

export default DeleteNews;