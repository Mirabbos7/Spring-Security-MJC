import React from "react";
import {Container, Nav, Navbar,} from "react-bootstrap";
import "./Header.css"

const Header = () => {
    return (
        <header className="header">
            <Navbar collapseOnSelect expand="lg">
                <Container>
                    <Navbar.Brand href="#home">
                        <img
                            src={require("../../assets/img/news-book.png")}
                            className="d-inline-block align-top"
                            alt="React Bootstrap logo"
                        />
                    </Navbar.Brand>
                    <Navbar.Toggle aria-controls="responsive-navbar-nav"/>
                    <Navbar.Collapse id="responsive-navbar-nav">
                        <Nav className="me-auto">
                            <Nav.Link href="/" className="navbar">HOME</Nav.Link>
                            <Nav.Link href="/" className="navbar">NEWS</Nav.Link>
                            <Nav.Link href="/" className="navbar">ABOUT</Nav.Link>
                        </Nav>
                        <Nav>
                            <Nav.Link href="/" className="navbar">SIGN IN</Nav.Link>
                            <Nav.Link href="/signup" className="navbar">SIGN UP</Nav.Link>
                        </Nav>
                    </Navbar.Collapse>
                </Container>
            </Navbar>
        </header>
    );
};

export default Header;