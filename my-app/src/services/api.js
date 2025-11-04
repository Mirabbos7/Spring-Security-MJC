const API_BASE_URL = 'http://localhost:8082/api/v1';

// Helper function to get auth token from user object
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

// Helper function to create headers
const getHeaders = () => {
    const token = getAuthToken();
    return {
        'Content-Type': 'application/json',
        ...(token && { 'Authorization': `Bearer ${token}` })
    };
};

// ============ NEWS API ============

// Get all news (using /getAll endpoint)
export const getAllNews = async () => {
    try {
        const response = await fetch(
            `${API_BASE_URL}/news/getAll`,
            {
                method: 'GET',
                headers: getHeaders()
            }
        );

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return await response.json();
    } catch (error) {
        console.error('Error fetching all news:', error);
        throw error;
    }
};

// Get news count
export const getNewsCount = async () => {
    try {
        const response = await fetch(
            `${API_BASE_URL}/news/count`,
            {
                method: 'GET',
                headers: getHeaders()
            }
        );

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const count = await response.text();
        return parseInt(count, 10);
    } catch (error) {
        console.error('Error getting news count:', error);
        return 0;
    }
};

// Get all authors
export const getAuthors = async () => {
    try {
        const response = await fetch(
            `${API_BASE_URL}/news/authors`,
            {
                method: 'GET',
                headers: getHeaders()
            }
        );

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return await response.json();
    } catch (error) {
        console.error('Error fetching authors:', error);
        throw error;
    }
};

// Get news with pagination
export const getNewsPaginated = async (page = 0, size = 5, sortBy = 'createDate,desc') => {
    try {
        const response = await fetch(
            `${API_BASE_URL}/news?page=${page}&size=${size}&sortBy=${sortBy}`,
            {
                method: 'GET',
                headers: getHeaders()
            }
        );

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return await response.json();
    } catch (error) {
        console.error('Error fetching paginated news:', error);
        throw error;
    }
};

// Get news by ID
export const getNewsById = async (id) => {
    try {
        const response = await fetch(
            `${API_BASE_URL}/news/${id}`,
            {
                method: 'GET',
                headers: getHeaders()
            }
        );

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return await response.json();
    } catch (error) {
        console.error('Error fetching news by id:', error);
        throw error;
    }
};

// Create news
export const createNews = async (newsData) => {
    try {
        const response = await fetch(
            `${API_BASE_URL}/news`,
            {
                method: 'POST',
                headers: getHeaders(),
                body: JSON.stringify(newsData)
            }
        );

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return await response.json();
    } catch (error) {
        console.error('Error creating news:', error);
        throw error;
    }
};

// Update news
export const updateNews = async (id, newsData) => {
    try {
        const response = await fetch(
            `${API_BASE_URL}/news/${id}`,
            {
                method: 'PATCH',
                headers: getHeaders(),
                body: JSON.stringify(newsData)
            }
        );

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return await response.json();
    } catch (error) {
        console.error('Error updating news:', error);
        throw error;
    }
};

// Delete news
export const deleteNews = async (id) => {
    try {
        const response = await fetch(
            `${API_BASE_URL}/news/${id}`,
            {
                method: 'DELETE',
                headers: getHeaders()
            }
        );

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return true;
    } catch (error) {
        console.error('Error deleting news:', error);
        throw error;
    }
};

// Search news by params
export const searchNews = async (params) => {
    try {
        const queryParams = new URLSearchParams();

        if (params.tagName) queryParams.append('tag_name', params.tagName);
        if (params.tagId) queryParams.append('tag_id', params.tagId);
        if (params.authorName) queryParams.append('author_name', params.authorName);
        if (params.title) queryParams.append('title', params.title);
        if (params.content) queryParams.append('content', params.content);

        const response = await fetch(
            `${API_BASE_URL}/news/search?${queryParams.toString()}`,
            {
                method: 'GET',
                headers: getHeaders()
            }
        );

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return await response.json();
    } catch (error) {
        console.error('Error searching news:', error);
        throw error;
    }
};

// Get tags by news ID
export const getTagsByNewsId = async (newsId) => {
    try {
        const response = await fetch(
            `${API_BASE_URL}/news/${newsId}/tag`,
            {
                method: 'GET',
                headers: getHeaders()
            }
        );

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return await response.json();
    } catch (error) {
        console.error('Error fetching tags by news id:', error);
        throw error;
    }
};

// Get author by news ID
export const getAuthorByNewsId = async (newsId) => {
    try {
        const response = await fetch(
            `${API_BASE_URL}/news/${newsId}/author`,
            {
                method: 'GET',
                headers: getHeaders()
            }
        );

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return await response.json();
    } catch (error) {
        console.error('Error fetching author by news id:', error);
        throw error;
    }
};

// Get comments by news ID
export const getCommentsByNewsId = async (newsId) => {
    try {
        const response = await fetch(
            `${API_BASE_URL}/news/${newsId}/comment`,
            {
                method: 'GET',
                headers: getHeaders()
            }
        );

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return await response.json();
    } catch (error) {
        console.error('Error fetching comments by news id:', error);
        throw error;
    }
};

// ============ AUTHORS API ============

export const getAllAuthors = async (page = 0, size = 1000, sortBy = 'name,asc') => {
    try {
        const response = await fetch(
            `${API_BASE_URL}/author/readAll?page=${page}&size=${size}&sortBy=${sortBy}`,
            {
                method: 'GET',
                headers: getHeaders()
            }
        );

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return await response.json();
    } catch (error) {
        console.error('Error fetching authors:', error);
        throw error;
    }
};

export const getAuthorById = async (id) => {
    try {
        const response = await fetch(
            `${API_BASE_URL}/author/${id}`,
            {
                method: 'GET',
                headers: getHeaders()
            }
        );

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return await response.json();
    } catch (error) {
        console.error('Error fetching author by id:', error);
        throw error;
    }
};

export const createAuthor = async (authorData) => {
    try {
        const response = await fetch(
            `${API_BASE_URL}/author/create`,
            {
                method: 'POST',
                headers: getHeaders(),
                body: JSON.stringify(authorData)
            }
        );

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return await response.json();
    } catch (error) {
        console.error('Error creating author:', error);
        throw error;
    }
};

export const updateAuthor = async (id, authorData) => {
    try {
        const response = await fetch(
            `${API_BASE_URL}/author/${id}`,
            {
                method: 'PATCH',
                headers: getHeaders(),
                body: JSON.stringify(authorData)
            }
        );

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return await response.json();
    } catch (error) {
        console.error('Error updating author:', error);
        throw error;
    }
};

export const deleteAuthor = async (id) => {
    try {
        const response = await fetch(
            `${API_BASE_URL}/author/${id}`,
            {
                method: 'DELETE',
                headers: getHeaders()
            }
        );

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return true;
    } catch (error) {
        console.error('Error deleting author:', error);
        throw error;
    }
};

// ============ TAGS API ============

export const getTags = async (page = 0, size = 1000, sortBy = 'name,asc') => {
    try {
        const response = await fetch(
            `${API_BASE_URL}/tag?page=${page}&size=${size}&sortBy=${sortBy}`,
            {
                method: 'GET',
                headers: getHeaders()
            }
        );

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return await response.json();
    } catch (error) {
        console.error('Error fetching tags:', error);
        throw error;
    }
};

export const getTagById = async (id) => {
    try {
        const response = await fetch(
            `${API_BASE_URL}/tag/${id}`,
            {
                method: 'GET',
                headers: getHeaders()
            }
        );

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return await response.json();
    } catch (error) {
        console.error('Error fetching tag by id:', error);
        throw error;
    }
};

export const createTag = async (tagData) => {
    try {
        const response = await fetch(
            `${API_BASE_URL}/tag`,
            {
                method: 'POST',
                headers: getHeaders(),
                body: JSON.stringify(tagData)
            }
        );

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return await response.json();
    } catch (error) {
        console.error('Error creating tag:', error);
        throw error;
    }
};

export const updateTag = async (id, tagData) => {
    try {
        const response = await fetch(
            `${API_BASE_URL}/tag/${id}`,
            {
                method: 'PATCH',
                headers: getHeaders(),
                body: JSON.stringify(tagData)
            }
        );

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return await response.json();
    } catch (error) {
        console.error('Error updating tag:', error);
        throw error;
    }
};

export const deleteTag = async (id) => {
    try {
        const response = await fetch(
            `${API_BASE_URL}/tag/${id}`,
            {
                method: 'DELETE',
                headers: getHeaders()
            }
        );

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return true;
    } catch (error) {
        console.error('Error deleting tag:', error);
        throw error;
    }
};