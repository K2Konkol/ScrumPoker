import React, { useState, useCallback, useEffect } from 'react';
import useWebSocket, { ReadyState } from 'react-use-websocket';
import {Message} from "./Message";

export const WebSocketDemo = () => {
    //Public API that will echo messages sent to it back to the client
    const socketUrl = 'ws://localhost:8080/play';

    const [messageHistory, setMessageHistory] = useState([]);

    const { sendMessage, lastMessage, readyState } = useWebSocket(socketUrl);

    useEffect(() => {
        if (lastMessage !== null) {
            // @ts-ignore
            setMessageHistory((prev) => prev.concat(lastMessage as Message));
        }
    }, [lastMessage, setMessageHistory]);


    const handleClickSendMessage = useCallback(() => sendMessage(JSON.stringify({player: "Krzysiek", rank: 1} as Message)), []);

    const connectionStatus = {
        [ReadyState.CONNECTING]: 'Connecting',
        [ReadyState.OPEN]: 'Open',
        [ReadyState.CLOSING]: 'Closing',
        [ReadyState.CLOSED]: 'Closed',
        [ReadyState.UNINSTANTIATED]: 'Uninstantiated',
    }[readyState];

    return (
        <div>
            <button
                onClick={handleClickSendMessage}
                disabled={readyState !== ReadyState.OPEN}
            >
                Click Me to send 'Hello'
            </button>
            <span>The WebSocket is currently {connectionStatus}</span>
            {lastMessage ? <span>Last message: {lastMessage.data}</span> : null}
            <ul>
                {messageHistory.map((message, idx) => (
                    // @ts-ignore
                    <span key={idx}>{message ? message.data as Message : null}</span>
                ))}
            </ul>
        </div>
    );
};
