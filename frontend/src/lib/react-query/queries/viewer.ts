import {
    useQuery,
    useMutation,
    useQueryClient,
    UseQueryResult,
} from "@tanstack/react-query";

import { QUERY_KEYS } from "../queryKeys";
import axios from "axios";
import Cookies from "js-cookie";
import { Viewer } from "../../types";

const baseUrl = "http://localhost:8080";
const ax = axios.create({ baseURL: baseUrl });

export const useAddViewer = ({ story }: Viewer) => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationKey: [QUERY_KEYS.ADD_STORY_VIEW, story],
        mutationFn: async () => {
            try {
                const res = await ax.post(`/stories/${story}/views`, {
                    story: story,
                    viewedAt: new Date().toISOString()
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
                queryKey: [QUERY_KEYS.GET_STORY_VIEWS, story]
            });
        },
    });
}

export const useGetStoryViews = ({ story }: Viewer): UseQueryResult<Viewer[], unknown> => {
    return useQuery({
        queryKey: [QUERY_KEYS.GET_STORY_VIEWS, story],
        queryFn: async () => {
            try {
                const res = await ax.get(`/stories/${story}/views?page=0&size=1000`, {
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
        select: (data) => data?._embedded?.viewerDTOes
    });
}
