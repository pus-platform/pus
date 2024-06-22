import React, { createContext, useContext, useEffect, useRef, useState, ReactNode } from 'react';
import { Client, StompSubscription } from '@stomp/stompjs';
import { useQueryClient } from '@tanstack/react-query';
import { useUserContext } from './AuthContext';
import { Message } from '../lib/types';
import { useGetGroupChats } from '@/lib/react-query/queries/groupChat';
import { useToast } from '@/components/ui/use-toast';
import { QUERY_KEYS } from '@/lib/react-query/queryKeys';

interface MessageContextType {
    subscribe: (destination: string, callback: (msg: any) => void) => StompSubscription | undefined;
    sendMessage: (destination: string, body: any) => void;
    onMessageReceived: (msg: any) => void;
}

const initialContext: MessageContextType = {
    subscribe: (destination: string, callback: (msg: any) => void) => {
        console.log(destination, callback)
        return undefined;
    },
    sendMessage: () => { },
    onMessageReceived: () => { },
};

const MessageContext = createContext<MessageContextType>(initialContext);

export const useMessage = (): MessageContextType => {
    const context = useContext(MessageContext);
    if (!context) {
        throw new Error("useMessage must be used within a MessageProvider");
    }
    return context;
};

interface MessageProviderProps {
    children: ReactNode;
}

export const MessageProvider: React.FC<MessageProviderProps> = ({ children }) => {
    const clientRef = useRef<Client | null>(null);
    const [connected, setConnected] = useState<boolean>(false);
    const { data: groups, isPending: isGroupLoading } = useGetGroupChats();
    const { user, isLoading: isUserLoading } = useUserContext();
    const subscriptions: StompSubscription[] = [];
    const queryClient = useQueryClient();
    const { toast } = useToast();

    useEffect(() => {
        clientRef.current = new Client({
            brokerURL: 'ws://localhost:8080/ws',
            onConnect: () => {
                console.log("Connected");
                setConnected(true);
            },
            onDisconnect: () => {
                console.log("Disconnected");
                setConnected(false);
            },
            onStompError: (frame) => {
                setConnected(false);
                console.error('Broker reported error: ' + frame.headers['message']);
                console.error('Additional details: ' + frame.body);
            },
        });

        clientRef.current.activate();

        return () => {
            if (clientRef.current) {
                clientRef.current.deactivate();
            }
        };
    }, []);

    useEffect(() => {
        if (connected && !isUserLoading && user && groups && !isGroupLoading) {
            groups?.forEach(group => subscriptions.push(subscribe(`/topic/group-${group.id}`, onMessageReceived)));
            subscriptions.push(subscribe(`/user/${user.id}/queue/messages`, onMessageReceived));
            subscriptions.push(subscribe(`/user/${user.id}/queue/notifications`, onNotificationReceived));
        }
    }, [connected]);

    const subscribe = (destination: string, callback: (msg: any) => void): StompSubscription | undefined => {
        if (connected && clientRef.current) {
            return clientRef.current.subscribe(destination, callback);
        }
    };

    const sendMessage = (destination: string, body: any): void => {
        if (connected && clientRef.current) {
            clientRef.current.publish({ destination, body: JSON.stringify(body) });
            console.log("Message sent", body, destination)
        }
    };

    const onNotificationReceived = (ntf: any): void => {
        const notification = JSON.parse(ntf.body);
        queryClient.invalidateQueries({
            queryKey: [QUERY_KEYS.GET_NOTIFICATIONS]
        })
        toast({
            title: notification.content,
            description: "",
        })
    }

    const onMessageReceived = (msg: any): void => {
        const message: Message = JSON.parse(msg.body);
        queryClient.invalidateQueries({
            queryKey: [QUERY_KEYS.GET_GROUP_CHAT_BY_ID, message.receiverGroup]
        })
        queryClient.invalidateQueries({
            queryKey: [QUERY_KEYS.GET_MESSAGES_BY_USER_ID, message.sender.id]
        })
        toast({
            title: message.sender.username,
            description: message.messageContent,
        })
    };

    return (
        <MessageContext.Provider value={{
            subscribe,
            sendMessage,
            onMessageReceived,
        }}>
            {children}
        </MessageContext.Provider>
    );
};
