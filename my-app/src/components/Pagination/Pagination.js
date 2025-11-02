import React from 'react';
import _ from "lodash";

function Pagination(props) {
    let array = paginationRange(props.totalPage, props.page, props.limit, props.siblings);
    return (
        <ul className="pagination pagination-md justify-content-end">
            <li className="page-item"><span onClick={() => props.onPageChange("&laquo;")} className="page-link">&laquo;</span></li>
            <li className="page-item"><span onClick={() => props.onPageChange("&lsaquo;")} className="page-link">&lsaquo;</span></li>
            {array.map(value => {
                if (value === props.page) {
                    return (
                        <li key={value} className="page-item active"><span onClick={() => props.onPageChange(value)} className="page-link">{value}</span></li>
                    )
                }
                else{
                    return (
                        <li key={value} className="page-item"><span onClick={() => props.onPageChange(value)} className="page-link">{value}</span></li>
                    )
                }
            })}
            <li className="page-item"><span onClick={() => props.onPageChange("&rsaquo;")} className="page-link">&rsaquo;</span></li>
            <li className="page-item"><span onClick={() => props.onPageChange("&raquo;")} className="page-link">&raquo;</span></li>
        </ul>
    );
}

export default Pagination;

export const paginationRange = (totalPage, page, limit, siblings) => {
    let totalPageNoInArray = 7 + siblings;
    if(totalPageNoInArray >= totalPage){
        return _.range(1, totalPage + 1);
    }
    let leftSiblingsIndex = Math.max(page - siblings, 1);
    let rightSiblingsIndex = Math.min(page + siblings, totalPage);
    let showLeftDots = leftSiblingsIndex > 2;
    let showRightDots = rightSiblingsIndex < totalPage - 2;
    if(!showLeftDots && showRightDots){
        let leftItemsCount = 5 * siblings;
        let leftRange = _.range(1,leftItemsCount + 1);
        return [...leftRange, " ...", totalPage];
    }
    else if(showLeftDots && !showRightDots){
        let rightItemsCount = 5 * siblings;
        let rightRange = _.range(totalPage - rightItemsCount + 1, totalPage + 1);
        return [1, "... ", ...rightRange];
    }
    else{
        let middleRange = _.range(leftSiblingsIndex, rightSiblingsIndex + 1);
        return [1, "... ", ...middleRange, " ...", totalPage];
    }
}