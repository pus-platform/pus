import {
    useQuery,
    useMutation,
    useQueryClient,
    UseQueryResult,
} from "@tanstack/react-query";

import { QUERY_KEYS } from "../queryKeys";
import axios from "axios";
import Cookies from "js-cookie";
import { ReplyToStory as StoryReply } from "../../types";

const baseUrl = "http://localhost:8080";
const ax = axios.create({ baseURL: baseUrl });

export const useCreateStoryReply = ({ story }: StoryReply) => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationKey: [QUERY_KEYS.ADD_STORY_REPLY, story],
        mutationFn: async ({ replyContent }: StoryReply) => {
            try {
                const res = await ax.post(`/stories/${story}/replies`, {
                    replyContent
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
                queryKey: [QUERY_KEYS.GET_STORY_REPLIES, story]
            });
        }
    });
}

export const useGetStoryReplies = ({ story }: StoryReply): UseQueryResult<StoryReply[], unknown> => {
    return useQuery({
        queryKey: [QUERY_KEYS.GET_STORY_REPLIES, story],
        queryFn: async () => {
            try {
                const res = await ax.get(`/stories/${story}/replies?page=0&size=1000`, {
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
        enabled: !!story,
        notifyOnChangeProps: ['data', 'status', 'isSuccess'],
        select: (data) => data?._embedded?.replyToStoryDTOes
    });
}

export const useUpdateStoryReply = ({ story, id }: StoryReply) => {
    return useMutation({
        mutationKey: [QUERY_KEYS.UPDATE_STORY_REPLY_BY_ID, story, id],
        mutationFn: async () => {
            try {
                const res = await ax.put(`/stories/${story}/replies/${id}`, {}, {
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

export const useDeleteStoryReply = ({ story, id }: StoryReply) => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationKey: [QUERY_KEYS.DELETE_STORY_REPLY_BY_ID, story, id],
        mutationFn: async () => {
            try {
                const res = await ax.delete(`/stories/${story}/replies/${id}`, {
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
                queryKey: [QUERY_KEYS.GET_STORY_REPLIES, story]
            });
        }
    });
}
