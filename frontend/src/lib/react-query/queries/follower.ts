import {
    useQuery,
    useMutation,
    useQueryClient,
    UseQueryResult,
} from "@tanstack/react-query";

import { QUERY_KEYS } from "../queryKeys";
import axios from "axios";
import Cookies from "js-cookie";
import { Follower } from "../../types";

const baseUrl = "http://localhost:8080";
const ax = axios.create({ baseURL: baseUrl });

export const useGetFollowers = ({ followedId }: Follower): UseQueryResult<Follower[], unknown> => {
    return useQuery({
        queryKey: [QUERY_KEYS.GET_USER_FOLLOWERS, followedId],
        queryFn: async () => {
            try {
                const res = await ax.get(`/users/${followedId}/followers?page=0&size=1000`, {
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
        enabled: !!followedId,
        notifyOnChangeProps: ['data', 'status', 'isSuccess'],
        select: (data) => data?._embedded?.followerDTOes,
    });
}

export const useGetFollowing = ({ followerId }: Follower): UseQueryResult<Follower[], unknown> => {
    return useQuery({
        queryKey: [QUERY_KEYS.GET_USER_FOLLOWING, followerId],
        queryFn: async () => {
            try {
                const res = await ax.get(`/users/${followerId}/following?page=0&size=1000`, {
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
        enabled: !!followerId,
        notifyOnChangeProps: ['data', 'status', 'isSuccess'],
        select: (data) => data?._embedded?.followerDTOes,
    });
}

export const useAddFollower = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationKey: [QUERY_KEYS.CREATE_USER_FOLLOWER],
        mutationFn: async ({ followedId, followerId }: Follower) => {
            try {
                const res = await ax.post(`/users/${followerId}/followers`, {
                    followed: {
                        id: followedId
                    }
                }, {
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
                queryKey: [QUERY_KEYS.GET_USER_FOLLOWERS]
            });
            queryClient.invalidateQueries({
                queryKey: [QUERY_KEYS.GET_USER_FOLLOWING]
            });
        },
    });
}

export const useDeleteFollower = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationKey: [QUERY_KEYS.DELETE_USER_FOLLOWER_BY_ID],
        mutationFn: async ({ followerId, followedId }: Follower) => {
            try {
                const res = await ax.delete(`/users/${followedId}/followers/${followerId}`, {
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
                queryKey: [QUERY_KEYS.GET_USER_FOLLOWERS]
            });
            queryClient.invalidateQueries({
                queryKey: [QUERY_KEYS.GET_USER_FOLLOWING]
            });
        },
    });
}
