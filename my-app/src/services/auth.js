import axios from "axios";

const API_URL = "http://localhost:8082/";

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
                localStorage.setItem("user", JSON.stringify(response.data));
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

export const logout = () => {
    localStorage.removeItem("user");
    window.location.href = "/";
};

export const getCurrentUser = () => {
    return JSON.parse(localStorage.getItem("user"));
};