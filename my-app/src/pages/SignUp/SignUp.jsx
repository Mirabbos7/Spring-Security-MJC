import React, {useState} from "react";
import "./SignUp.css"
import {Button, Col, Container, Form, Row} from "react-bootstrap";
import Header from "../../components/Header/Header";
import Footer from "../../components/Footer/Footer";
import { useNavigate } from 'react-router-dom';
import { register } from "../../services/auth";

const SignUp = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const onChange = (e) => {
        const { name, value } = e.target;
        if (name === 'username') {
            setUsername(value);
        } else if (name === 'password') {
            setPassword(value);
        }
    };

    const onSignupClick = () => {
        if (username.length === 0 || password.length === 0) {
            setError("Fill in blanks fields");
        } else if (username.length < 3 || username.length > 30) {
            setError("User name length must not be less than 3 and greater than 30");
        } else if (password.length < 4 || password.length > 30) {
            setError("Password length must not be less than 4 and greater than 30");
        } else {
            register(username,password)
                .then( () => {
                    setError("You registered successfully.");
                    navigate("/");
                })
                .catch( () => {
                    setError("Registered failed. Please try again.");
                });
        }
    };

    return (
        <Container>

            <Header />

            <Row>
                <Col md="4">
                    <div className="body-div">
                        <h1 className="signup-heading">Sign up</h1>
                        <Form className="form">
                            <Form.Group controlId="usernameId" className="form-group">
                                <Form.Control
                                    type="text"
                                    name="username"
                                    placeholder="Username"
                                    value={username}
                                    onChange={onChange}
                                />
                            </Form.Group>
                            <Form.Group controlId="passwordId" className="form-group">
                                <Form.Control
                                    type="password"
                                    name="password"
                                    placeholder="Password"
                                    value={password}
                                    onChange={onChange}
                                />
                            </Form.Group>
                        </Form>
                        <div className="div-button">
                            <Button color="primary" className="signup-button" onClick={onSignupClick}>SIGN UP</Button>
                            <p className="signup-error">{error}</p>
                        </div>
                    </div>
                </Col>
            </Row>

            <Footer />

        </Container>
    );
}

export default SignUp;