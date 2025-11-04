import React from 'react';

function SelectLimit(props) {
    return (
        <select
            onChange={(e) => props.onLimitChange(e.target.value)}
            className="form-select"
            style={styles.select}
            defaultValue="10"
        >
            <option value="3">3</option>
            <option value="5">5</option>
            <option value="10">10</option>
            <option value="20">20</option>
            <option value="50">50</option>
        </select>
    );
}

const styles = {
    select: {
        padding: '8px 12px',
        border: '1px solid #ddd',
        borderRadius: '6px',
        background: 'white',
        cursor: 'pointer',
        fontSize: '14px',
        minWidth: '80px'
    }
};

export default SelectLimit;