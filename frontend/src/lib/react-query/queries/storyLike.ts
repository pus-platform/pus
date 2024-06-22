import {
    useQuery,
    useMutation,
    useQueryClient,
    UseQueryResult,
} from "@tanstack/react-query";

import { QUERY_KEYS } from "../queryKeys";
import axios from "axios";
import Cookies from "js-cookie";
import { StoryLike } from "../../types";

const baseUrl = "http://localhost:8080";
const ax = axios.create({ baseURL: baseUrl });

export const useLikeStory = ({ story }: StoryLike) => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationKey: [QUERY_KEYS.ADD_STORY_LIKE, story],
        mutationFn: async () => {
            try {
                const res = await ax.post(`/stories/${story}/likes`, {
                    story: story,
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
                queryKey: [QUERY_KEYS.GET_STORY_LIKES, story]
            });
        }
    });
}

export const useGetStoryLikes = ({ story }: StoryLike): UseQueryResult<StoryLike[], unknown> => {
    return useQuery({
        queryKey: [QUERY_KEYS.GET_STORY_LIKES, story],
        queryFn: async () => {
            try {
                const res = await ax.get(`/stories/${story}/likes?page=0&size=1000`, {
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
        select: (data) => data?._embedded?.storyLikeDTOes,
    });
}

export const useUpdateStoryLike = ({ story, userId }: StoryLike) => {
    return useMutation({
        mutationKey: [QUERY_KEYS.UPDATE_STORY_LIKE_BY_ID, story, userId],
        mutationFn: async () => {
            try {
                const res = await ax.put(`/stories/${story}/likes/${userId}`, {}, {
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

export const useDeleteStoryLike = ({ story, userId }: StoryLike) => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationKey: [QUERY_KEYS.DELETE_STORY_LIKE_BY_ID, story, userId],
        mutationFn: async () => {
            try {
                const res = await ax.delete(`/stories/${story}/likes/${userId}`, {
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
                queryKey: [QUERY_KEYS.GET_STORY_LIKES, story]
            });
        }
    });
}
