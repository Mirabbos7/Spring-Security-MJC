import React from 'react';
import _ from 'lodash';

const Pagination = (props) => {
    const array = paginationRange(props.totalPage, props.page, props.siblings);

    return (
        <div style={styles.paginationWrapper}>
            <button
                onClick={() => props.onPageChange("first")}
                disabled={props.page === 1}
                style={{...styles.button, ...(props.page === 1 && styles.disabled)}}
            >
                «
            </button>
            <button
                onClick={() => props.onPageChange("prev")}
                disabled={props.page === 1}
                style={{...styles.button, ...(props.page === 1 && styles.disabled)}}
            >
                ‹
            </button>

            {array.map((value, index) => {
                if (typeof value === 'string' && value.includes('...')) {
                    return (
                        <span key={`dots-${index}`} style={styles.dots}>
                            {value}
                        </span>
                    );
                }

                return (
                    <button
                        key={value}
                        onClick={() => props.onPageChange(value)}
                        style={{
                            ...styles.button,
                            ...(value === props.page && styles.active)
                        }}
                    >
                        {value}
                    </button>
                );
            })}

            <button
                onClick={() => props.onPageChange("next")}
                disabled={props.page === props.totalPage}
                style={{...styles.button, ...(props.page === props.totalPage && styles.disabled)}}
            >
                ›
            </button>
            <button
                onClick={() => props.onPageChange("last")}
                disabled={props.page === props.totalPage}
                style={{...styles.button, ...(props.page === props.totalPage && styles.disabled)}}
            >
                »
            </button>
        </div>
    );
};

const paginationRange = (totalPage, page, siblings = 1) => {
    const totalPageNoInArray = 7 + siblings;

    if (totalPageNoInArray >= totalPage) {
        return _.range(1, totalPage + 1);
    }

    const leftSiblingsIndex = Math.max(page - siblings, 1);
    const rightSiblingsIndex = Math.min(page + siblings, totalPage);

    const showLeftDots = leftSiblingsIndex > 2;
    const showRightDots = rightSiblingsIndex < totalPage - 2;

    if (!showLeftDots && showRightDots) {
        const leftItemsCount = 3 + 2 * siblings;
        const leftRange = _.range(1, leftItemsCount + 1);
        return [...leftRange, "...", totalPage];
    } else if (showLeftDots && !showRightDots) {
        const rightItemsCount = 3 + 2 * siblings;
        const rightRange = _.range(totalPage - rightItemsCount + 1, totalPage + 1);
        return [1, "...", ...rightRange];
    } else {
        const middleRange = _.range(leftSiblingsIndex, rightSiblingsIndex + 1);
        return [1, "...", ...middleRange, "...", totalPage];
    }
};

const styles = {
    paginationWrapper: {
        display: 'flex',
        gap: '8px',
        alignItems: 'center',
        justifyContent: 'center'
    },
    button: {
        padding: '8px 12px',
        border: '1px solid #ddd',
        borderRadius: '6px',
        background: 'white',
        cursor: 'pointer',
        fontSize: '14px',
        minWidth: '40px',
        transition: 'all 0.2s',
        fontWeight: '500'
    },
    active: {
        background: '#4169E1',
        color: 'white',
        borderColor: '#4169E1'
    },
    disabled: {
        cursor: 'not-allowed',
        opacity: 0.5,
        background: '#f5f5f5'
    },
    dots: {
        padding: '8px',
        color: '#999'
    }
};

export default Pagination;