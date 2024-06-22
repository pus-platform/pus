import {
    useQuery,
    useMutation,
    useQueryClient,
    UseQueryResult,
} from "@tanstack/react-query";

import { QUERY_KEYS } from "../queryKeys";
import axios from "axios";
import Cookies from "js-cookie";
import { CommentLike } from "../../types";

const baseUrl = "http://localhost:8080";
const ax = axios.create({ baseURL: baseUrl });

export const useLikeComment = ({ comment }:CommentLike) => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationKey: [QUERY_KEYS.CREATE_COMMENT_LIKE, comment],
        mutationFn: async ({ postId, comment }:CommentLike) => {
            try {
                const res = await ax.post(`/posts/${postId}/comments/${comment}/likes`, {
                    comment: comment,
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
                queryKey: [QUERY_KEYS.GET_POST_COMMENTS]
            });
        },
    });
}

export const useGetCommentLikes = ({ postId, comment }:CommentLike): UseQueryResult<CommentLike[], unknown> => {
    return useQuery({
        queryKey: [QUERY_KEYS.GET_COMMENT_LIKES, comment],
        queryFn: async () => {
            try {
                const res = await ax.get(`/posts/${postId}/comments/${comment}/likes?page=0&size=1000`, {
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
        enabled: !!comment,
        select: (data) => data?._embedded?.commentLikeDTOes,
        notifyOnChangeProps: ['data', 'status', 'isSuccess'],
    });
}

export const useUpdateCommentLike = ({ comment }:CommentLike) => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationKey: [QUERY_KEYS.UPDATE_COMMENT_LIKE_BY_ID, comment],
        mutationFn: async ({ postId, comment, userId }:CommentLike) => {
            try {
                const res = await ax.put(`/posts/${postId}/comments/${comment}/likes/${userId}`, {}, {
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
                queryKey: [QUERY_KEYS.GET_COMMENT_LIKES, comment]
            });
        },
    });
}

export const useDeleteCommentLike = ({ comment }:CommentLike) => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationKey: [QUERY_KEYS.DELETE_COMMENT_LIKE_BY_ID, comment],
        mutationFn: async ({ postId, comment, userId }:CommentLike) => {
            try {
                const res = await ax.delete(`/posts/${postId}/comments/${comment}/likes/${userId}`, {
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
                queryKey: [QUERY_KEYS.GET_POST_COMMENTS]
            });
        },
    });
}
