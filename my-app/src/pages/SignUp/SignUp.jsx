import React, { useState } from "react";
import "./SignUp.css";
import { useNavigate } from 'react-router-dom';
import { register } from "../../services/auth";

const SignUp = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const onSignupClick = async (e) => {
        e.preventDefault();
        setError('');

        // Validation
        if (!username.trim() || !password.trim() || !confirmPassword.trim()) {
            setError("Please fill in all fields");
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

        if (password !== confirmPassword) {
            setError("Passwords do not match");
            return;
        }

        setLoading(true);

        try {
            await register(username, password);
            // Registration successful, redirect to login
            navigate("/");
        } catch (err) {
            console.error("Registration failed:", err);
            setError(err.response?.data?.message || "Registration failed. Username may already exist.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="signup-page">
            {/* Header */}
            <header className="signup-header">
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
                        <a href="/">NEWS</a>
                        <a href="/">ABOUT</a>
                        <a href="/" className="nav-btn">SIGN IN</a>
                        <a href="/signup" className="nav-btn active">SIGN UP</a>
                    </nav>
                </div>
            </header>

            {/* Main Content */}
            <div className="signup-container">
                <div className="signup-card">
                    <h1 className="signup-title">Sign Up</h1>

                    <form onSubmit={onSignupClick} className="signup-form">
                        <div className="form-field">
                            <label htmlFor="username">Username</label>
                            <input
                                id="username"
                                type="text"
                                value={username}
                                onChange={(e) => setUsername(e.target.value)}
                                placeholder="Enter your username"
                                disabled={loading}
                            />
                        </div>

                        <div className="form-field">
                            <label htmlFor="password">Password</label>
                            <input
                                id="password"
                                type="password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                placeholder="Enter your password"
                                disabled={loading}
                            />
                        </div>

                        <div className="form-field">
                            <label htmlFor="confirmPassword">Confirm Password</label>
                            <input
                                id="confirmPassword"
                                type="password"
                                value={confirmPassword}
                                onChange={(e) => setConfirmPassword(e.target.value)}
                                placeholder="Confirm your password"
                                disabled={loading}
                            />
                        </div>

                        {error && <div className="error-message">{error}</div>}

                        <button
                            type="submit"
                            className="signup-btn"
                            disabled={loading}
                        >
                            {loading ? 'SIGNING UP...' : 'SIGN UP'}
                        </button>

                        <div className="signin-link">
                            Already have an account? <a href="/">Sign in here</a>
                        </div>
                    </form>
                </div>
            </div>

            {/* Footer */}
            <footer className="signup-footer">
                © 2025 MJC School Student. All Rights Reserved
            </footer>
        </div>
    );
};

export default SignUp;