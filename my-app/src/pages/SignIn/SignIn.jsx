import React, { useState } from 'react';
import "./SignIn.css";
import { login } from "../../services/auth";
import { useNavigate } from 'react-router-dom';

const SignIn = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const onChange = (e) => {
        const { name, value } = e.target;
        if (name === 'username') {
            setUsername(value);
        } else {
            setPassword(value);
        }
        // Clear error when user starts typing
        if (error) setError('');
    };

    const onLoginClick = async (e) => {
        e.preventDefault();

        // Validation
        if (username.length === 0 || password.length === 0) {
            setError("Fill in blank fields");
            return;
        }
        if (username.length < 3 || username.length > 30) {
            setError("Username must be between 3 and 30 characters");
            return;
        }
        if (password.length < 4 || password.length > 30) {
            setError("Password must be between 4 and 30 characters");
            return;
        }

        setError("");
        setLoading(true);

        try {
            const data = await login(username, password);

            if (data && (data.token || data.accessToken)) {
                navigate("/news");
            } else {
                setError("Invalid response from server");
            }
        } catch (error) {
            console.error("Login failed:", error);

            if (error.response) {
                setError(error.response.data.message || "Invalid credentials");
            } else if (error.request) {
                setError("Cannot connect to server");
            } else {
                setError("Login failed. Please try again");
            }
        } finally {
            setLoading(false);
        }
    };

    const handleKeyPress = (e) => {
        if (e.key === 'Enter') {
            onLoginClick(e);
        }
    };

    return (
        <div className="signin-page">
            {/* Header */}
            <header className="signin-header">
                <div className="header-content">
                    <div className="logo-section">
                        <img
                            src={require("../../assets/img/news-book.png")}
                            alt="News Management"
                            className="logo-img"
                        />
                        <span className="logo-text">News Management</span>
                    </div>
                    <nav className="nav-links">
                        <a href="/">HOME</a>
                        <a href="/news">NEWS</a>
                        <a href="/about">ABOUT</a>
                        <a href="/signin" className="active">SIGN IN</a>
                        <a href="/signup">SIGN UP</a>
                    </nav>
                </div>
            </header>

            {/* Login Form */}
            <div className="signin-container">
                <div className="login-card">
                    <h1 className="login-title">Login</h1>

                    <form onSubmit={onLoginClick} className="login-form">
                        <div className="form-field">
                            <label htmlFor="username">Username</label>
                            <input
                                id="username"
                                type="text"
                                name="username"
                                placeholder="Enter your username"
                                value={username}
                                onChange={onChange}
                                onKeyPress={handleKeyPress}
                                autoComplete="username"
                                disabled={loading}
                            />
                        </div>

                        <div className="form-field">
                            <label htmlFor="password">Password</label>
                            <input
                                id="password"
                                type="password"
                                name="password"
                                placeholder="Enter your password"
                                value={password}
                                onChange={onChange}
                                onKeyPress={handleKeyPress}
                                autoComplete="current-password"
                                disabled={loading}
                            />
                        </div>

                        {error && (
                            <div className="error-message">
                                {error}
                            </div>
                        )}

                        <button
                            type="submit"
                            className="signin-button"
                            disabled={loading}
                        >
                            {loading ? 'SIGNING IN...' : 'SIGN IN'}
                        </button>
                    </form>

                    <div className="signup-link">
                        Don't have an account? <a href="/signup">Sign up</a>
                    </div>
                </div>
            </div>

            {/* Footer */}
            <footer className="signin-footer">
                © 2025 MJC School Student. All Rights Reserved
            </footer>
        </div>
    );
};

export default SignIn;