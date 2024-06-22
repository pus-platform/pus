import {
    useQuery,
    useMutation,
    useQueryClient,
    UseQueryResult,
} from "@tanstack/react-query";
import { QUERY_KEYS } from "../queryKeys";
import axios from "axios";
import Cookies from "js-cookie";
import { Bookmark } from "../../types";

const baseUrl = "http://localhost:8080";
const ax = axios.create({ baseURL: baseUrl });

export const useSavePost = ({ postId }: Bookmark) => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationKey: [QUERY_KEYS.CREATE_USER_BOOKMARK, postId],
        mutationFn: async ({ postId }: Bookmark) => {
            const res = await ax.post('bookmarks', {
                post: {
                    id: postId,
                },
            }, {
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${Cookies.get("token")}`
                }
            });
            return res.data;
        },
        onSettled: () => {
            queryClient.invalidateQueries({
                queryKey: [QUERY_KEYS.GET_USER_BOOKMARKS]
            });
        },
    });
};

export const useDeleteSavedPost = ({ postId }: Bookmark) => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationKey: [QUERY_KEYS.DELETE_USER_BOOKMARK_BY_ID, postId],
        mutationFn: async ({ postId }: Bookmark) => {
            const res = await ax.delete(`bookmarks/${postId}`, {
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${Cookies.get("token")}`
                }
            });
            return res.data;
        },
        onSettled: () => {
            queryClient.invalidateQueries({
                queryKey: [QUERY_KEYS.GET_USER_BOOKMARKS]
            });
        },
    });
};

export const useGetSavedPosts = (): UseQueryResult<Bookmark[], unknown> => {
    return useQuery({
        queryKey: [QUERY_KEYS.GET_USER_BOOKMARKS],
        queryFn: async () => {
            const res = await ax.get('bookmarks?page=0&size=1000', {
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${Cookies.get("token")}`
                }
            });
            return res.data;
        },
        notifyOnChangeProps: ['data', 'status', 'isSuccess'],
        select: (data) => data?._embedded?.bookmarkDTOes,
    });
};
