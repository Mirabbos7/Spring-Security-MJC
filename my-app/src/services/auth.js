import axios from "axios";

const API_URL = "http://localhost:8082/";

// Register a new user
export const register = (username, password) => {
    console.log("Register service called with:", { username });

    return axios.post(API_URL + "sign-up", {
        username,
        password
    })
        .then(response => {
            console.log("Registration response:", response.data);
            return response.data;
        })
        .catch(error => {
            console.error("Registration service error:", error.response);
            throw error;
        });
};

// Login user
export const login = (username, password) => {
    console.log("Auth service - login called with:", username);

    return axios
        .post(API_URL + "sign-in", {
            username,
            password
        })
        .then((response) => {
            console.log("Auth service - Response received:", response);
            console.log("Auth service - Response data:", response.data);

            if (response.data && (response.data.token || response.data.accessToken)) {
                console.log("Auth service - Saving to localStorage");

                // Store user data with token
                const userData = {
                    ...response.data,
                    username: username
                };

                localStorage.setItem("user", JSON.stringify(userData));
                console.log("Auth service - Saved:", localStorage.getItem("user"));
            } else {
                console.warn("Auth service - No token in response!");
            }

            return response.data;
        })
        .catch((error) => {
            console.error("Auth service - Error:", error);
            throw error;
        });
};

// Logout user
export const logout = () => {
    console.log("Logging out user...");
    localStorage.removeItem("user");
    window.location.href = "/";
};

// Get current logged in user
export const getCurrentUser = () => {
    try {
        const userData = localStorage.getItem("user");
        return userData ? JSON.parse(userData) : null;
    } catch (error) {
        console.error("Error getting current user:", error);
        return null;
    }
};

// Check if user is authenticated
export const isAuthenticated = () => {
    const userData = localStorage.getItem('user');
    if (!userData) return false;

    try {
        const parsed = JSON.parse(userData);
        return !!(parsed.token || parsed.accessToken);
    } catch {
        return false;
    }
};

// Get authentication token
export const getAuthToken = () => {
    const userData = localStorage.getItem('user');
    if (!userData) return null;

    try {
        const parsed = JSON.parse(userData);
        return parsed.token || parsed.accessToken;
    } catch {
        return null;
    }
};