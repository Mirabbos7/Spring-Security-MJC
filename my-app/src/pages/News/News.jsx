import React, { useEffect, useState } from "react";
import "./News.css";
import AddNews from "../../components/Popup/AddNews";
import DeleteNews from "../../components/Popup/DeleteNews";
import EditNews from "../../components/Popup/EditNews";
import Pagination from "../../components/Pagination/Pagination";
import SelectLimit from "../../components/Pagination/SelectLimit";
import { useNavigate } from "react-router-dom";
import { logout } from "../../services/auth";

const News = () => {
    const [allNews, setAllNews] = useState([]);
    const [currentPage, setCurrentPage] = useState(1);
    const [pageSize, setPageSize] = useState(10);
    const [totalPages, setTotalPages] = useState(0);
    const [totalElements, setTotalElements] = useState(0);
    const [search, setSearch] = useState("");
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [sortBy, setSortBy] = useState("createDate,desc");
    const [deleteModalOpen, setDeleteModalOpen] = useState(false);
    const [editModalOpen, setEditModalOpen] = useState(false);
    const [selectedNews, setSelectedNews] = useState(null);
    const navigate = useNavigate();

    // Get auth token
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

    // Fetch all news with pagination from backend
    const fetchAllNews = async () => {
        try {
            const token = getAuthToken();
            if (!token) {
                navigate("/");
                return;
            }

            setLoading(true);
            setError("");

            // Convert 1-based page to 0-based for API
            const apiPage = currentPage - 1;

            // Fetch paginated news
            const newsResponse = await fetch(
                `http://localhost:8082/api/v1/news?page=${apiPage}&size=${pageSize}&sortBy=${sortBy}`,
                {
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json'
                    }
                }
            );

            if (!newsResponse.ok) {
                if (newsResponse.status === 401 || newsResponse.status === 403) {
                    logout();
                    navigate("/");
                    return;
                }
                throw new Error('Failed to fetch news');
            }

            const data = await newsResponse.json();

            // Extract news list and pagination info
            let newsData = [];
            let total = 0;
            let pages = 0;

            if (data.newsList) {
                // Backend returns NewsPageDtoResponse with newsList
                newsData = data.newsList;
                total = data.totalElements || newsData.length;
                pages = data.totalPages || Math.ceil(total / pageSize);
            } else if (Array.isArray(data)) {
                // Fallback if backend returns array
                newsData = data;
                total = data.length;
                pages = 1;
            }

            // Fetch author and tags for each news item
            const enrichedNews = await Promise.all(
                newsData.map(async (news) => {
                    try {
                        // Fetch author
                        const authorResponse = await fetch(
                            `http://localhost:8082/api/v1/news/${news.id}/author`,
                            {
                                headers: {
                                    'Authorization': `Bearer ${token}`,
                                    'Content-Type': 'application/json'
                                }
                            }
                        );

                        let authorName = 'Unknown';
                        if (authorResponse.ok) {
                            const authorData = await authorResponse.json();
                            authorName = authorData.name || 'Unknown';
                        }

                        // Fetch tags
                        const tagsResponse = await fetch(
                            `http://localhost:8082/api/v1/news/${news.id}/tag`,
                            {
                                headers: {
                                    'Authorization': `Bearer ${token}`,
                                    'Content-Type': 'application/json'
                                }
                            }
                        );

                        let tagNames = [];
                        if (tagsResponse.ok) {
                            const tagsData = await tagsResponse.json();
                            tagNames = tagsData.map(tag => tag.name || tag);
                        }

                        return {
                            ...news,
                            authorName,
                            tagNames
                        };
                    } catch (err) {
                        console.error(`Error fetching details for news ${news.id}:`, err);
                        return {
                            ...news,
                            authorName: 'Unknown',
                            tagNames: []
                        };
                    }
                })
            );

            setAllNews(enrichedNews);
            setTotalElements(total);
            setTotalPages(pages);
        } catch (err) {
            console.error('Error fetching data:', err);
            setError("Failed to load news. Please try again.");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchAllNews();
    }, [currentPage, pageSize, sortBy]);

    const handleAddNews = async (newNews) => {
        // Refresh the current page to include new news
        await fetchAllNews();
    };

    const handleDeleteNews = async (newsId) => {
        setAllNews(allNews.filter(news => news.id !== newsId));
        setDeleteModalOpen(false);

        // Adjust page if current page becomes empty
        if (allNews.length === 1 && currentPage > 1) {
            setCurrentPage(currentPage - 1);
        } else {
            await fetchAllNews();
        }
    };

    const handleEditNews = async (updatedNews) => {
        setEditModalOpen(false);
        await fetchAllNews();
    };

    const openDeleteModal = (news) => {
        setSelectedNews(news);
        setDeleteModalOpen(true);
    };

    const openEditModal = (news) => {
        setSelectedNews(news);
        setEditModalOpen(true);
    };

    const handleSignOut = () => {
        logout();
    };

    const handlePageChange = (page) => {
        if (page === "first") {
            setCurrentPage(1);
        } else if (page === "prev") {
            if (currentPage > 1) setCurrentPage(currentPage - 1);
        } else if (page === "next") {
            if (currentPage < totalPages) setCurrentPage(currentPage + 1);
        } else if (page === "last") {
            setCurrentPage(totalPages);
        } else if (typeof page === 'number') {
            setCurrentPage(page);
        }
    };

    const handleLimitChange = (newLimit) => {
        setPageSize(parseInt(newLimit));
        setCurrentPage(1); // Reset to first page when changing limit
    };

    // Client-side search filter
    const filteredNews = allNews.filter(
        (item) =>
            item.title.toLowerCase().includes(search.toLowerCase()) ||
            item.content.toLowerCase().includes(search.toLowerCase())
    );

    const formatDate = (dateString) => {
        const date = new Date(dateString);
        return date.toLocaleString('en-US', {
            month: 'long',
            day: 'numeric',
            year: 'numeric',
            hour: 'numeric',
            minute: '2-digit',
            hour12: true
        });
    };

    if (loading) {
        return (
            <div className="news-page">
                <div className="loading">Loading...</div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="news-page">
                <div className="error">{error}</div>
            </div>
        );
    }

    return (
        <div className="news-page">
            {/* Header */}
            <header style={{
                background: '#071f36',
                color: 'white',
                padding: '20px 80px',
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center'
            }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '20px' }}>
                    <img
                        src={require("../../assets/img/news-book.png")}
                        alt="News Management"
                        style={{ height: '40px' }}
                    />
                    <span style={{ fontSize: '24px', fontWeight: 'bold' }}>News Management</span>
                </div>
                <nav style={{ display: 'flex', gap: '30px', alignItems: 'center' }}>
                    <a href="/news" style={{ color: 'white', textDecoration: 'none' }}>HOME</a>
                    <a href="/news" style={{ color: 'white', textDecoration: 'none' }}>NEWS</a>
                    <a href="/news" style={{ color: 'white', textDecoration: 'none' }}>ABOUT</a>
                    <button
                        onClick={handleSignOut}
                        style={{
                            background: '#e74c3c',
                            color: 'white',
                            border: 'none',
                            padding: '8px 20px',
                            borderRadius: '5px',
                            cursor: 'pointer',
                            fontWeight: 'bold'
                        }}
                    >
                        SIGN OUT
                    </button>
                </nav>
            </header>

            <div className="news-wrapper">
                <div className="news-search-bar">
                    <div className="search-box">
                        <input
                            type="text"
                            className="search-input"
                            placeholder="Search News..."
                            value={search}
                            onChange={(e) => setSearch(e.target.value)}
                        />
                        <span className="search-icon">🔍</span>
                    </div>
                    <AddNews onAdd={handleAddNews} />
                </div>

                <div className="news-filter-bar">
                    <h3>{totalElements} News</h3>
                    <div style={{ display: 'flex', gap: '15px', alignItems: 'center' }}>
                        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                            <span style={{ fontSize: '14px', color: '#666' }}>Show:</span>
                            <SelectLimit onLimitChange={handleLimitChange} />
                        </div>
                        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                            <span style={{ fontSize: '14px', color: '#666' }}>Sort by:</span>
                            <select
                                className="sort-select"
                                value={sortBy}
                                onChange={(e) => setSortBy(e.target.value)}
                            >
                                <option value="createDate,desc">Date Created ▼</option>
                                <option value="createDate,asc">Date Created ▲</option>
                                <option value="title,asc">Title A-Z</option>
                                <option value="title,desc">Title Z-A</option>
                            </select>
                        </div>
                    </div>
                </div>

                <div className="news-card-container">
                    {filteredNews.map((news) => (
                        <div key={news.id} className="news-card">
                            <div>
                                <h4 className="news-title">{news.title}</h4>
                                <p className="news-date">{formatDate(news.createDate)}</p>
                                <p className="news-text">{news.content}</p>

                                <div style={{ marginTop: '15px' }}>
                                    <div className="label">AUTHOR</div>
                                    <div className="value">{news.authorName || 'Unknown'}</div>
                                </div>

                                {news.tagNames && news.tagNames.length > 0 && (
                                    <div style={{ marginTop: '10px' }}>
                                        <div className="label">TAGS</div>
                                        <div className="tag-list">
                                            {news.tagNames.map((tag, index) => (
                                                <span key={index} className="tag">
                                                    {tag}
                                                </span>
                                            ))}
                                        </div>
                                    </div>
                                )}
                            </div>

                            <div className="news-actions">
                                <button
                                    onClick={() => openEditModal(news)}
                                    style={{
                                        background: 'none',
                                        border: 'none',
                                        cursor: 'pointer',
                                        fontSize: '20px'
                                    }}
                                >
                                    ✏️
                                </button>
                                <button
                                    onClick={() => openDeleteModal(news)}
                                    style={{
                                        background: 'none',
                                        border: 'none',
                                        cursor: 'pointer',
                                        fontSize: '20px'
                                    }}
                                >
                                    🗑️
                                </button>
                            </div>
                        </div>
                    ))}
                </div>

                {/* Pagination Controls */}
                {totalPages > 1 && (
                    <div className="pagination-section">
                        <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
                            <span style={{ fontSize: '14px', color: '#666' }}>Items per page:</span>
                            <SelectLimit onLimitChange={handleLimitChange} />
                        </div>

                        <Pagination
                            totalPage={totalPages}
                            page={currentPage}
                            siblings={1}
                            onPageChange={handlePageChange}
                        />

                        <div style={{ fontSize: '14px', color: '#666' }}>
                            Page {currentPage} of {totalPages}
                        </div>
                    </div>
                )}
            </div>

            {deleteModalOpen && selectedNews && (
                <DeleteNews
                    news={selectedNews}
                    onDelete={handleDeleteNews}
                    onClose={() => setDeleteModalOpen(false)}
                />
            )}

            {editModalOpen && selectedNews && (
                <EditNews
                    news={selectedNews}
                    onEdit={handleEditNews}
                    onClose={() => setEditModalOpen(false)}
                />
            )}

            <footer style={{
                background: '#071f36',
                color: 'grey',
                textAlign: 'center',
                padding: '15px',
                marginTop: '40px'
            }}>
                © 2025 MJC School Student. All Rights Reserved
            </footer>
        </div>
    );
};

export default News;