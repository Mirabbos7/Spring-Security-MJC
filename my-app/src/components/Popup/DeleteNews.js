import React, { useState } from 'react';
import Button from 'react-bootstrap/Button';
import Modal from 'react-bootstrap/Modal';
import axios from "axios";

function DeleteNews(props) {
    const [show, setShow] = useState(false);
    const handleClose = () => setShow(false);
    const handleShow = () => setShow(true);

    const deleteNews = () => {
        axios.delete("http://localhost:8082/news/id/" + props.id)
            .then(response => {
                console.log(response.data);
            })
        setShow(false)
        window.location.reload();
    }

    return (
        <>
            <img
                src={require("../../assets/img/basket.png")}
                style={{float: "right", cursor: "pointer"}}
                alt="basket icone"
                onClick={handleShow}
            />

            <Modal show={show} onHide={handleClose}>
                <Modal.Header closeButton>
                    <Modal.Title>
                        <img
                            src={require("../../assets/img/basket1.png")}
                            style={{marginLeft: "200px"}}
                            alt="basket icone"
                        />
                    </Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <center>
                        <h4>Do you really want to delete this news?</h4>
                    </center>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" style={{marginRight: "315px"}} onClick={handleClose}>
                        Cancel
                    </Button>
                    <Button variant="danger" onClick={deleteNews}>
                        Delete
                    </Button>
                </Modal.Footer>
            </Modal>
        </>
    );
}

export default DeleteNews;