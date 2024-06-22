import {
    useQuery,
    useMutation,
    useQueryClient,
    UseQueryResult,
} from "@tanstack/react-query";

import { QUERY_KEYS } from "../queryKeys";
import axios from "axios";
import Cookies from "js-cookie";
import { GroupMember } from "../../types";

const baseUrl = "http://localhost:8080/group-chats";
const ax = axios.create({ baseURL: baseUrl });

export const useGetGroupMembers = ({ group }: GroupMember): UseQueryResult<GroupMember[], unknown> => {
    return useQuery({
        queryKey: [QUERY_KEYS.GET_GROUP_CHAT_MEMBERS, group],
        queryFn: async () => {
            try {
                const res = await ax.get(`/${group}/members?page=0&size=1000`, {
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
        enabled: !!group,
        notifyOnChangeProps: ['data', 'status', 'isSuccess'],
        select: (data) => data?._embedded?.groupMemberDTOes,
    });
}

export const useAddGroupMember = ({ group }: GroupMember) => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationKey: [QUERY_KEYS.ADD_GROUP_CHAT_MEMBER, group],
        mutationFn: async () => {
            try {
                const res = await ax.post(`/${group}/members`, {}, {
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
                queryKey: [QUERY_KEYS.GET_GROUP_CHAT_MEMBERS, group]
            });
        },
    });
}

export const useDeleteGroupMember = ({ group, userId }: GroupMember) => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationKey: [QUERY_KEYS.DELETE_GROUP_CHAT_MEMBER_BY_ID, group, userId],
        mutationFn: async () => {
            try {
                const res = await ax.delete(`/${group}/members/${userId}`, {
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
                queryKey: [QUERY_KEYS.GET_GROUP_CHAT_MEMBERS, group]
            });
        },
    });
}

export const useUpdateGroupMember = ({ group, userId }: GroupMember) => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationKey: [QUERY_KEYS.UPDATE_GROUP_CHAT_MEMBER_BY_ID, group, userId],
        mutationFn: async () => {
            try {
                const res = await ax.put(`/${group}/members/${userId}`, {}, {
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
                queryKey: [QUERY_KEYS.GET_GROUP_CHAT_MEMBERS, group]
            });
        },
    });
}
