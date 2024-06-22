import {
    useQuery,
    useMutation,
    useQueryClient,
    UseQueryResult,
} from "@tanstack/react-query";

import { QUERY_KEYS } from "../queryKeys";
import axios from "axios";
import Cookies from "js-cookie";
import { GroupChat } from "../../types";

const baseUrl = "http://localhost:8080";
const ax = axios.create({ baseURL: baseUrl });

export const useCreateGroupChat = () => {
    return useMutation({
        mutationKey: [QUERY_KEYS.CREATE_GROUP_CHAT],
        mutationFn: async (data:GroupChat) => {
            try {
                const res = await ax.post(`group-chats`, data, {
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
    });
}

export const useGetGroupChats = (): UseQueryResult<GroupChat[], unknown> => {
    return useQuery({
        queryKey: [QUERY_KEYS.GET_GROUP_CHATS],
        queryFn: async () => {
            try {
                const res = await ax.get(`group-chats?page=0&size=1000`, {
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
        notifyOnChangeProps: ['data', 'status', 'isSuccess'],
        select: (data) => data?._embedded?.groupChatDTOes,
    });
}

export const useGetGroupChat = ({ id }: GroupChat): UseQueryResult<GroupChat, unknown> => {
    return useQuery({
        queryKey: [QUERY_KEYS.GET_GROUP_CHAT_BY_ID, id],
        queryFn: async () => {
            try {
                const res = await ax.get(`group-chats/${id}`, {
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
        enabled: !!id,
        notifyOnChangeProps: ['data', 'status', 'isSuccess'],
    });
}

export const useDeleteGroupChat = ({ id }: GroupChat) => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationKey: [QUERY_KEYS.DELETE_GROUP_CHAT_BY_ID, id],
        mutationFn: async () => {
            try {
                const res = await ax.delete(`group-chats/${id}`, {
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
        onSettled: () => {
            queryClient.invalidateQueries({
                queryKey: [QUERY_KEYS.GET_GROUP_CHATS]
            });
        },
    });
}

export const useUpdateGroupChat = ({ id }: GroupChat) => {
    return useMutation({
        mutationKey: [QUERY_KEYS.UPDATE_GROUP_CHAT_BY_ID, id],
        mutationFn: async (data: GroupChat) => {
            try {
                const res = await ax.put(`group-chats/${data.id}`, data, {
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
    });
}
