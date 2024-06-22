import {
    useQuery,
    useMutation,
    useQueryClient,
    UseQueryResult,
} from "@tanstack/react-query";

import { QUERY_KEYS } from "../queryKeys";
import axios from "axios";
import Cookies from "js-cookie";
import { PostLike } from "../../types";

const baseUrl = "http://localhost:8080";
const ax = axios.create({ baseURL: baseUrl });

export const useLikePost = ({ postId }: PostLike) => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationKey: [QUERY_KEYS.CREATE_POST_LIKE, postId],
        mutationFn: async ({ postId }: PostLike) => {
            try {
                const res = await ax.post(`/posts/${postId}/likes`, {
                    post: { id: postId },
                    reaction: "LIKE"
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
                queryKey: [QUERY_KEYS.GET_POST_LIKES, postId]
            });
        },
    });
}

export const useGetPostLikes = ({ postId }: PostLike): UseQueryResult<PostLike[], unknown> => {
    return useQuery({
        queryKey: [QUERY_KEYS.GET_POST_LIKES, postId],
        queryFn: async () => {
            try {
                const res = await ax.get(`/posts/${postId}/likes?page=0&size=1000`, {
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
        enabled: !!postId,
        notifyOnChangeProps: ['data', 'status', 'isSuccess'],
        select: (data) => data?._embedded?.postLikeDTOes,
    });
}

export const useUpdatePostLike = ({ postId, userId }: PostLike) => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationKey: [QUERY_KEYS.UPDATE_POST_LIKE_BY_ID, postId, userId],
        mutationFn: async () => {
            try {
                const res = await ax.put(`/posts/${postId}/likes/${userId}`, {}, {
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
                queryKey: [QUERY_KEYS.GET_POST_LIKES, postId]
            });
        },
    });
}

export const useDeletePostLike = ({ postId, userId }: PostLike) => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationKey: [QUERY_KEYS.DELETE_POST_LIKE_BY_ID, postId, userId],
        mutationFn: async () => {
            try {
                const res = await ax.delete(`/posts/${postId}/likes/${userId}`, {
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
                queryKey: [QUERY_KEYS.GET_POST_LIKES, postId]
            });
        },
    });
}
