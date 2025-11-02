import React, { useState } from 'react';
import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';
import Modal from 'react-bootstrap/Modal';

function ShowPopup() {

    const [title, setTitle] = useState('');
    const [content, setContent] = useState('');
    const [show, setShow] = useState(false);

    const handleClose = () => setShow(false);
    const handleShow = () => setShow(true);

    const getValues = event => {
        event.preventDefault();
        if(title.length === 0 || content.length === 0){
            document.getElementById('aboutError').innerHTML = "Fill in blanks fields";
        }
        else if(title.length < 6 || title.length > 30){
            document.getElementById('aboutError').innerHTML = "Title field must not be less than 6 and greater than 30 characters";
        }
        else if(content.length < 12 || content > 1000){
            document.getElementById('aboutError').innerHTML = "Content field must not be less than 12 and greater than 1000 characters";
        }
        else{
            let data = {title,content};
            fetch("news/add-news",{
                method: "POST",
                headers:{
                    'Accept':'application/json',
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            }).then(data => console.log(data));
            window.location.reload();
            handleClose();
        }
    }



    return (
        <>
            <button onClick={handleShow} className="add-news">Add News</button>

            <Modal show={show} onHide={handleClose}>
                <Modal.Header closeButton>
                    <Modal.Title>New news</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form>
                        <Form.Group className="mb-3" controlId="exampleForm.ControlInput1">
                            <Form.Label>TITLE</Form.Label>
                            <Form.Control
                                type="title"
                                value={title}
                                name="title"
                                autoFocus
                                onChange={event => setTitle(event.target.value)}
                            />
                        </Form.Group>
                        <Form.Group
                            className="mb-3"
                            controlId="exampleForm.ControlTextarea1"
                        >
                            <Form.Label>CONTENT</Form.Label>
                            <Form.Control
                                as="textarea"
                                rows={5}
                                value={content}
                                name="content"
                                onChange={event => setContent(event.target.value)}
                            />
                        </Form.Group>
                        <Form.Group>
                            <Form.Label>TAGS</Form.Label><br/>
                            <Button style={{borderRadius: "18px"}}>Add tags +</Button>
                        </Form.Group>
                    </Form>
                    <center><p style={{color: "red"}} id="aboutError"></p></center>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" style={{marginRight: "330px"}} onClick={handleClose}>
                        Cancel
                    </Button>
                    <Button onClick={getValues} type="submit" variant="primary">
                        Save
                    </Button>
                </Modal.Footer>
            </Modal>
        </>
    );
}

export default ShowPopup;