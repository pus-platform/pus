import {
    useQuery,
    useMutation,
    useQueryClient,
    UseQueryResult,
} from "@tanstack/react-query";
import { QUERY_KEYS } from "../queryKeys";
import axios from "axios";
import Cookies from "js-cookie";
import { Comment } from "../../types";

const baseUrl = "http://localhost:8080";
const ax = axios.create({ baseURL: baseUrl });

export const useAddComment = ({ post }: Comment) => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationKey: [QUERY_KEYS.CREATE_COMMENT, post],
        mutationFn: async (comment: Comment) => {
            try {
                const res = await ax.post(`/posts/${comment.post}/comments`, { ...comment }, {
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
                queryKey: [QUERY_KEYS.GET_POST_COMMENTS, post]
            });
        },
    });
}

export const useUpdateComment = ({ post, id }: Comment) => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationKey: [QUERY_KEYS.UPDATE_COMMENT_BY_ID, post, id],
        mutationFn: async (comment: Comment) => {
            try {
                const res = await ax.put(`/posts/${comment.post}/comments/${comment.id}`, { ...comment }, {
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
                queryKey: [QUERY_KEYS.GET_POST_COMMENTS, post]
            });
        },
    });
}

export const useDeletePostComment = ({ post, id }: Comment) => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationKey: [QUERY_KEYS.DELETE_COMMENT_BY_ID, post, id],
        mutationFn: async () => {
            try {
                const res = await ax.delete(`/posts/${post}/comments/${id}`, {
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
                queryKey: [QUERY_KEYS.GET_POST_COMMENTS, post]
            });
        },
    });
}

export const useGetCommentReplies = ({ post, id }: Comment): UseQueryResult<Comment[], unknown> => {
    return useQuery({
        queryKey: [QUERY_KEYS.GET_COMMENT_REPLIES, post, id],
        queryFn: async () => {
            try {
                const res = await ax.get(`/posts/${post}/comments/${id}/replies?page=0&size=10000`, {
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
        enabled: !!post && !!id,
        select: (data) => data?._embedded?.commentDTOes,
        notifyOnChangeProps: ['data', 'status', 'isSuccess'],
    });
}

export const useCreateCommentReply = ({ post, commentId }: Comment) => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationKey: [QUERY_KEYS.ADD_COMMENT_REPLY, post, commentId],
        mutationFn: async (reply:Comment) => {
            try {
                const res = await ax.post(`/posts/${post}/comments/${commentId}/replies`, { ...reply, post }, {
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
                queryKey: [QUERY_KEYS.GET_COMMENT_REPLIES, post, commentId]
            });
            queryClient.invalidateQueries({
                queryKey: [QUERY_KEYS.GET_POST_COMMENTS, post]
            });
        },
    });
}

export const useGetPostComments = ({ post }: Comment): UseQueryResult<Comment[], unknown> => {
    return useQuery({
        queryKey: [QUERY_KEYS.GET_POST_COMMENTS, post],
        queryFn: async () => {
            try {
                const res = await ax.get(`/posts/${post}/comments?page=0&size=10000`, {
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
        enabled: !!post,
        notifyOnChangeProps: ['data', 'status', 'isSuccess'],
        select: (data) => data?._embedded?.commentDTOes,
    });
}
