import React, { Component } from "react";
import {BrowserRouter, Route, Routes} from "react-router-dom";
import SignIn from "./pages/SignIn/SignIn";
import SignUp from "./pages/SignUp/SignUp";
import News from "./pages/News/News";

class App extends Component {
    render() {
        return (
            <div>
                <BrowserRouter>
                    <Routes>
                        <Route exact path="/" element={<SignIn/>} />
                        <Route path="/signup" element={<SignUp/>} />
                        <Route path="/news" element={<News/>} />
                    </Routes>
                </BrowserRouter>
            </div>
        );
    }
}

export default App;