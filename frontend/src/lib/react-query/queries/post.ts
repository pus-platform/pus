import {
    useQuery,
    useMutation,
    useQueryClient,
    UseQueryResult,
} from "@tanstack/react-query";

import { QUERY_KEYS } from "../queryKeys";
import axios from "axios";
import Cookies from "js-cookie";
import { Post } from "../../types";

const baseUrl = "http://localhost:8080";
const ax = axios.create({ baseURL: baseUrl });

export const useGetUserPosts = ({ userId }: Post): UseQueryResult<Post[], unknown> => {
    return useQuery({
        queryKey: [QUERY_KEYS.GET_USER_POSTS, userId],
        queryFn: async () => {
            try {
                const res = await ax.get(`posts/user/${userId}?page=0&size=1000`, {
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
        notifyOnChangeProps: ['data', 'status', 'isRefetching'],
        select: (data) => data?._embedded?.postDTOes,
    });
}

export const useGetPosts = (): UseQueryResult<Post[], unknown> => {
    return useQuery({
        queryKey: [QUERY_KEYS.GET_POSTS],
        queryFn: async () => {
            try {
                const res = await ax.get(`posts?page=0&size=1000`, {
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
        notifyOnChangeProps: ['data', 'status', 'isRefetching'],
        select: (data) => data?._embedded?.postDTOes,
    });
}

export const useGetRecentPosts = (): UseQueryResult<Post[], unknown> => {
    return useQuery({
        queryKey: [QUERY_KEYS.GET_RECENT_POSTS],
        queryFn: async () => {
            try {
                const res = await ax.get(`posts/user/following/recent?page=${0}&size=${10000}`, {
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
        notifyOnChangeProps: ['data', 'status', 'isRefetching'],
        select: (data) => data?._embedded?.postDTOes,
    });
}

export const useGetPostById = ({ id }: Post): UseQueryResult<Post, unknown> => {
    return useQuery({
        queryKey: [QUERY_KEYS.GET_POST_BY_ID, id],
        queryFn: async () => {
            try {
                const res = await ax.get(`posts/${id}`, {
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
        notifyOnChangeProps: ['data', 'status', 'isRefetching'],
    });
}

export const useGetTrendingPosts = (): UseQueryResult<Post[], unknown> => {
    return useQuery({
        queryKey: [QUERY_KEYS.GET_TRENDING_POSTS],
        queryFn: async () => {
            try {
                const res = await ax.get(`posts/trending?page=0&size=1000`, {
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
        notifyOnChangeProps: ['data', 'status', 'isRefetching'],
        select: (data) => data?._embedded?.postDTOes,
    });
}

export const useCreatePost = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationKey: [QUERY_KEYS.CREATE_POST],
        mutationFn: async (data: Post) => {
            const payloadData = {
                // @ts-ignore
                post: `{ "caption": "${data.description}", "view": "PUBLIC" }`,
            };
            // @ts-ignore
            if (data.imgSrc !== undefined && data.imgSrc.length > 0)
                // @ts-ignore
                payloadData["file"] = data.imgSrc[0].file;

            const res = await ax.post(`/posts`, payloadData, {
                headers: {
                    "Content-Type": "multipart/form-data",
                    Authorization: `Bearer ${Cookies.get("token")}`,
                },
            });
            return res.data;
        },
        onSettled: () => {
            queryClient.invalidateQueries({
                queryKey: [QUERY_KEYS.GET_POSTS],
            });
        },
    });
};

export const useUpdatePost = ({ id }: Post) => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationKey: [QUERY_KEYS.UPDATE_POST_BY_ID, id],
        mutationFn: async (data: Post) => {
            try {
                const res = await ax.put(`posts/${id}`, data, {
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
                queryKey: [QUERY_KEYS.GET_POST_BY_ID, id]
            });
            queryClient.invalidateQueries({
                queryKey: [QUERY_KEYS.GET_TRENDING_POSTS]
            });
            queryClient.invalidateQueries({
                queryKey: [QUERY_KEYS.GET_USER_POSTS]
            });
            queryClient.invalidateQueries({
                queryKey: [QUERY_KEYS.GET_RECENT_POSTS]
            });
            queryClient.invalidateQueries({
                queryKey: [QUERY_KEYS.GET_COMMUNITY_POSTS]
            });
            queryClient.invalidateQueries({
                queryKey: [QUERY_KEYS.GET_FOLLOWING_USER_POSTS]
            });
        },
    });
}

export const useDeletePost = ({ id }: Post) => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationKey: [QUERY_KEYS.DELETE_POST_BY_ID, id],
        mutationFn: async () => {
            try {
                const res = await ax.delete(`posts/${id}`, {
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
                queryKey: [QUERY_KEYS.GET_TRENDING_POSTS]
            });
            queryClient.invalidateQueries({
                queryKey: [QUERY_KEYS.GET_USER_POSTS]
            });
            queryClient.invalidateQueries({
                queryKey: [QUERY_KEYS.GET_RECENT_POSTS]
            });
            queryClient.invalidateQueries({
                queryKey: [QUERY_KEYS.GET_COMMUNITY_POSTS]
            });
            queryClient.invalidateQueries({
                queryKey: [QUERY_KEYS.GET_FOLLOWING_USER_POSTS]
            });
        },
    });
}
