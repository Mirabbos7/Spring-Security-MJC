import React, {useEffect, useState} from "react";
import "./News.css"
import {Container} from "react-bootstrap";
import AddNews from "../../components/Popup/AddNews";
import DeleteNews from "../../components/Popup/DeleteNews";
import EditNews from "../../components/Popup/EditNews";
import Pagination from "../../components/Pagination/Pagination";
import SelectLimit from "../../components/Pagination/SelectLimit";
import Header from "../../components/Header/Header";
import Footer from "../../components/Footer/Footer";
import { getAllNews, getAuthors, getNewsCount } from "../../services/api";
import { useNavigate } from 'react-router-dom';

const News = () => {
    const [allNews, setAllNews] = useState([]);
    const [author, setAuthor] = useState([]);
    const [count, setCount] = useState("");
    const [currentPage, setCurrentPage] = useState(1);
    const [contentPerPage, setContentPerPage] = useState(3);
    const [search, setSearch] = useState("");
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const navigate = useNavigate();

    const indexOfLastPost = currentPage * contentPerPage;
    const indexOfFirstPost = indexOfLastPost - contentPerPage;
    let totalPage = Math.ceil(allNews.length / contentPerPage);

    useEffect(() => {
        const fetchData = async () => {
            try {
                setLoading(true);
                setError("");

                // Check if user is logged in
                const user = localStorage.getItem("user");
                if (!user) {
                    navigate("/");
                    return;
                }

                const [newsData, authorsData, countData] = await Promise.all([
                    getAllNews(),
                    getAuthors(),
                    getNewsCount()
                ]);

                setAllNews(newsData);
                setAuthor(authorsData);
                setCount(countData);
            } catch (err) {
                console.error("Error fetching data:", err);
                setError("Failed to load news. Please try again.");

                // If unauthorized, redirect to login
                if (err.message.includes("401") || err.message.includes("unauthorized")) {
                    localStorage.removeItem("user");
                    navigate("/");
                }
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, [navigate]);

    function handlePageChange(value){
        if(value === "&laquo;" || value === "... ") {
            setCurrentPage(1);
        } else if(value === "&lsaquo;") {
            if(currentPage !== 1) {
                setCurrentPage(currentPage - 1);
            }
        } else if(value === "&rsaquo;") {
            if(currentPage !== totalPage) {
                setCurrentPage(currentPage + 1);
            }
        } else if(value === "&raquo;" || value === " ...") {
            setCurrentPage(totalPage);
        } else {
            setCurrentPage(value);
        }
    }

    if (loading) {
        return (
            <Container>
                <Header />
                <center><h3>Loading...</h3></center>
                <Footer />
            </Container>
        );
    }

    if (error) {
        return (
            <Container>
                <Header />
                <center><h3 style={{color: "red"}}>{error}</h3></center>
                <Footer />
            </Container>
        );
    }

    return (
        <Container>
            <Header />

            <center>
                <div style={{width: "820px", height: "600px"}}>
                    <input
                        type="search"
                        className="search"
                        onChange={e => setSearch(e.target.value)}
                        placeholder="Search News...">
                    </input>
                    <AddNews />
                    <div>
                        <h3 className="news-count">{count} News</h3>
                        <select className="select">
                            <option value="dateCreated">Date Created</option>
                        </select>
                    </div>
                    {allNews
                        .filter(item => item.title.startsWith(search) || item.content.startsWith(search))
                        .slice(indexOfFirstPost, indexOfLastPost)
                        .map((item) => (
                            <div className="box" key={item.id}>
                                <center>
                                    <br/><h4><b>{item.title}</b></h4>
                                    <h6 className="create-date">{item.createDate}</h6>
                                    <div className="box-inner">
                                        <h6 className="content">{item.content}</h6>
                                        <h6 className="author">AUTHOR</h6>
                                        {author.filter(i => i.id === item.id).map((author) =>
                                            <h6 key={author.id} className="author-name">{author.name}</h6>
                                        )}
                                        <h6 className="tags">TAGS</h6>
                                        <div className="tags-sport">SPORT</div>
                                        <div className="tags-tennis">TENNIS</div>
                                        <DeleteNews id={item.id}/>
                                        <EditNews id={item.id}/>
                                    </div>
                                </center>
                            </div>
                        ))}
                    <div style={{marginTop: "400px", marginRight: "220px"}}>
                        <Pagination
                            totalPage={totalPage}
                            page={currentPage}
                            limit={contentPerPage}
                            siblings={1}
                            onPageChange={handlePageChange}
                        />
                        <SelectLimit onLimitChange={setContentPerPage}/>
                    </div>
                </div>
            </center>

            <Footer />
        </Container>
    );
};

export default News;