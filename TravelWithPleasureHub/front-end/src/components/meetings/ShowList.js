import React from "react";
import {NavLink} from "react-router-dom";

export default function getListWithAllMeetings(value) {
    return <ul className="list-group">
        {value.length < 1 ?
            <div className="alert alert-warning row h-100 justify-content-center align-items-center"> Nothing was
                founded :(
            </div>
            : value.map(item => (
                <NavLink className="nav-link" to={`/meetings/show-meeting/${item.id}`}>
                    <li key={item.id} className="list-group-item list-group-item-action flex-column align-items-start">
                        {item.header.charAt(0).toLocaleUpperCase() + item.header.slice(1)}.
                        Location: {item.location}. Time: {item.timeOfAction.replace("T", " ").substring(0, 16)}
                    </li>
                </NavLink>
            ))}

    </ul>
}