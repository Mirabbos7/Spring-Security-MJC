// src/utils/authUtils.js

/**
 * Get authentication token from localStorage
 * Checks multiple storage keys for compatibility
 */
export const getAuthToken = () => {
    // First try to get from 'user' key (format from auth.js)
    const userData = localStorage.getItem('user');
    if (userData) {
        try {
            const parsed = JSON.parse(userData);
            const token = parsed.token || parsed.accessToken;
            if (token) {
                console.log('Token found in user object');
                return token;
            }
        } catch (e) {
            console.warn('Failed to parse user data:', e);
        }
    }

    // Try to get directly from 'token' key (format from SignIn)
    const directToken = localStorage.getItem('token');
    if (directToken) {
        try {
            const parsed = JSON.parse(directToken);
            const token = parsed.token || directToken;
            console.log('Token found in token key');
            return token;
        } catch (e) {
            // If it's not JSON, return it directly (raw token string)
            console.log('Token found as raw string');
            return directToken;
        }
    }

    console.warn('No token found in localStorage');
    return null;
};

/**
 * Create headers for API requests with authentication
 */
export const getAuthHeaders = () => {
    const token = getAuthToken();
    return {
        'Content-Type': 'application/json',
        ...(token && { 'Authorization': `Bearer ${token}` })
    };
};

/**
 * Check if user is authenticated
 */
export const isAuthenticated = () => {
    const token = getAuthToken();
    return token !== null;
};

/**
 * Get current user data
 */
export const getCurrentUser = () => {
    const userData = localStorage.getItem('user');
    if (userData) {
        try {
            return JSON.parse(userData);
        } catch (e) {
            console.error('Error parsing user data:', e);
        }
    }

    const tokenData = localStorage.getItem('token');
    if (tokenData) {
        try {
            const parsed = JSON.parse(tokenData);
            return {
                token: parsed.token || tokenData,
                username: parsed.username || localStorage.getItem('username')
            };
        } catch (e) {
            return {
                token: tokenData,
                username: localStorage.getItem('username')
            };
        }
    }

    return null;
};