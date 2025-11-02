import React, {useState} from 'react';
import Button from 'react-bootstrap/Button';
import Modal from 'react-bootstrap/Modal';
import Form from "react-bootstrap/Form";

function EditNews(props) {

    const [show, setShow] = useState(false);
    const [title, setTitle] = useState('');
    const [content, setContent] = useState('');

    const handleClose = () => setShow(false);
    const handleShow = () => setShow(true);

    const updateNews = () => {
        let data = {title,content};
        fetch("news/update/" + props.id,{
            method: "PUT",
            headers:{
                'Accept':'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        }).then(data => console.log(data));
        setShow(false);
        window.location.reload();
    }

    return (
        <>
            <img
                src={require("../../assets/img/pencil.png")}
                style={{float: "right", cursor: "pointer"}}
                alt="pencile icone"
                onClick={handleShow}
            />

            <Modal show={show} onHide={handleClose}>
                <Modal.Header closeButton>
                    <Modal.Title>Edit news</Modal.Title>
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
                    </Form>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" style={{marginRight: "330px"}} onClick={handleClose}>
                        Cancel
                    </Button>
                    <Button onClick={updateNews} type="submit" variant="primary">
                        Save
                    </Button>
                </Modal.Footer>
            </Modal>
        </>
    );
}

export default EditNews;