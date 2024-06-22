import {
    useQuery,
    UseQueryResult,
} from "@tanstack/react-query";

import { QUERY_KEYS } from "../queryKeys";
import axios from "axios";
import Cookies from "js-cookie";
import { Message } from "../../types";

const baseUrl = "http://localhost:8080";
const ax = axios.create({ baseURL: baseUrl });

export const useGetMessages = ({ userId }): UseQueryResult<Message[], unknown> => {
    return useQuery({
        queryKey: [QUERY_KEYS.GET_MESSAGES],
        queryFn: async () => {
            try {
                const res = await ax.get(`/users/${userId}/messages?page=0&size=1000`, {
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": `Bearer ${Cookies.get("token")}`
                    }
                });
                return res.data;
            } catch (error) {
                console.error(error.message);
                return [];
            }
        },
        notifyOnChangeProps: ['data'],
        select: (data) => {
            // const messages = 
            return data?._embedded?.collectionModels
            // return messages.array.forEach((element: { _embedded: { messageDTOes: Message[]; }; }) => {
            //     return element._embedded.messageDTOes;
            // });
        },
    });
}

export const useGetMessagesByUserId = ({ userId, senderId }: Message): UseQueryResult<Message[], unknown> => {
    return useQuery({
        queryKey: [QUERY_KEYS.GET_MESSAGES_BY_USER_ID, userId],
        queryFn: async () => {
            try {
                const res = await ax.get(`users/${senderId}/messages/user/${userId}?page=0&size=1000`, {
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": `Bearer ${Cookies.get("token")}`
                    }
                });
                return res.data;
            } catch (error) {
                console.error(error.message);
                return [];
            }
        },
        enabled: !!userId,
        notifyOnChangeProps: ['data', 'status', 'isSuccess'],
        select: (data) => data?._embedded?.messageDTOes,
    });
}

export const useGetMessagesByGroupId = ({ groupId }: Message): UseQueryResult<Message[], unknown> => {
    return useQuery({
        queryKey: [QUERY_KEYS.GET_MESSAGES_BY_GROUP_ID, groupId],
        queryFn: async () => {
            try {
                const res = await ax.get(`/messages/group/${groupId}?page=0&size=1000`, {
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": `Bearer ${Cookies.get("token")}`
                    }
                });
                return res.data;
            } catch (error) {
                console.error(error.message);
                return [];
            }
        },
        enabled: !!groupId,
        notifyOnChangeProps: ['data'],
        select: (data) => data?._embedded?.messageDTOes,
    });
}
