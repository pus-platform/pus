import {
    useQuery,
    useMutation,
    useQueryClient,
    UseQueryResult,
} from "@tanstack/react-query";

import { QUERY_KEYS } from "../queryKeys";
import axios from "axios";
import Cookies from "js-cookie";
import { Story } from "../../types";

const baseUrl = "http://localhost:8080";
const ax = axios.create({ baseURL: baseUrl });

export const useGetStory = ({ id }: Story): UseQueryResult<Story, unknown> => {
    return useQuery({
        queryKey: [QUERY_KEYS.GET_STORY_BY_ID, id],
        queryFn: async () => {
            try {
                const res = await ax.get(`/stories/${id}`, {
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

export const useCreateStory = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationKey: [QUERY_KEYS.CREATE_STORY],
        mutationFn: async (data: Story) => {
            const payload = {
                story: `{ "view": "PUBLIC" }`,
            };
            if (data.image !== undefined)
                payload["file"] = data.image.file;
            const res = await ax.post(`/stories`, payload, {
                headers: {
                    "Content-Type": "multipart/form-data",
                    Authorization: `Bearer ${Cookies.get("token")}`,
                },
            }
            );
            return res.data;
        },
        onSettled: () => {
            queryClient.invalidateQueries({
                queryKey: [QUERY_KEYS.GET_USER_STORIES],
            });
        },
    });
};

export const useGetUserStories = ({ userId }: Story): UseQueryResult<Story[], unknown> => {
    return useQuery({
        queryKey: [QUERY_KEYS.GET_USER_STORIES, userId],
        queryFn: async () => {
            try {
                const res = await ax.get(`/stories/users/${userId}?page=0&size=1000`, {
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
        select: (data) => data?._embedded?.storyDTOes
    });
}

export const useDeleteStory = ({ id }: Story) => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationKey: [QUERY_KEYS.DELETE_STORY_BY_ID, id],
        mutationFn: async () => {
            try {
                const res = await ax.delete(`/stories/${id}`, {
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
                queryKey: [QUERY_KEYS.GET_USER_STORIES]
            });
        }
    });
}

export const useUpdateStory = ({ id }: Story) => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationKey: [QUERY_KEYS.UPDATE_STORY_BY_ID, id],
        mutationFn: async (data: Story) => {
            try {
                const res = await ax.put(`/stories/${id}`, data, {
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
                queryKey: [QUERY_KEYS.GET_STORY_BY_ID, id]
            });
        }
    });
}

export const useGetFollowingStories = ({ userId }: Story): UseQueryResult<Story[], unknown> => {
    return useQuery({
        queryKey: [QUERY_KEYS.GET_FOLLOWING_USER_STORIES, userId],
        queryFn: async () => {
            try {
                const res = await ax.get(`/stories/users/${userId}/followings?page=0&size=1000`, {
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
        select: (data) => data?._embedded?.storyDTOes
    });
}