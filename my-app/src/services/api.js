const getAuthHeader = () => {
    const user = JSON.parse(localStorage.getItem("user"));
    if (user && user.token) {
        return { Authorization: `Bearer ${user.token}` };
    }
    return {};
};

export const getAllNews = async () => {
    const response = await fetch('http://localhost:8082/api/v1/news/getAll', {
        headers: {
            'Content-Type': 'application/json',
            ...getAuthHeader()
        }
    });
    if (!response.ok) throw new Error('Failed to fetch all news');
    return response.json();
};

export const getAuthors = async () => {
    const response = await fetch('http://localhost:8082/api/v1/news/authors', {
        headers: {
            'Content-Type': 'application/json',
            ...getAuthHeader()
        }
    });
    if (!response.ok) throw new Error('Failed to fetch authors');
    return response.json();
};

export const getNewsCount = async () => {
    const response = await fetch('http://localhost:8082/api/v1/news/count', {
        headers: {
            'Content-Type': 'application/json',
            ...getAuthHeader()
        }
    });
    if (!response.ok) throw new Error('Failed to fetch news count');
    return response.text();
};