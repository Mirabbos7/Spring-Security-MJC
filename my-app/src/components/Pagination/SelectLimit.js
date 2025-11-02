import React from 'react';

function SelectLimit(props) {
    return (
        <select onChange={(e) => props.onLimitChange(e.target.value)} className="form-select">
            <option value="3">3</option>
            <option value="10">10</option>
            <option value="20">20</option>
            <option value="50">50</option>
        </select>
    );
}

export default SelectLimit;