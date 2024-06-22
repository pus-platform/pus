import { useEffect, useState } from "react";
import Button from "../common/Button/Button";
import TextInput from "../common/TextInput/TextInput";
import { useUserContext } from "@/context/AuthContext";
import { Message } from "@/lib/types";
import { multiFormatDateString } from "@/lib/utils";
import Loader from "../common/Loader/Loader";
import { SendHorizontal } from "lucide-react";
import { useMessage } from "@/context/MessageContext";
import { useGetGroupChat } from "@/lib/react-query/queries/groupChat";
import GroupAvatar from "../common/Avatar/GroupAvatar";

const GroupChatBox: React.FC<{ selectedGroupId: number }> = ({ selectedGroupId }) => {
    const { user: User, isLoading: isUserLoading } = useUserContext();
    const { data: selectedGroup, isPending: isGroupPending } = useGetGroupChat({ id: selectedGroupId });
    const initialConversation = selectedGroup?.messages;
    const [newMessage, setNewMessage] = useState("");
    const [conversation, setConversation] = useState([]);
    const { sendMessage } = useMessage();
    let messages = conversation || [];
    useEffect(() => {
        messages = conversation || [];
    }, [conversation, selectedGroupId]);
    useEffect(() => {
        setConversation(!!initialConversation ? [...initialConversation] : []);
    }, [initialConversation]);

    if (isUserLoading || isGroupPending)
        return (
            <Loader />
        )

    const handleSendMessage = (e: React.FormEvent, message: string) => {
        e.preventDefault();
        if (!selectedGroup || !message) return;

        const newMessageObj: Message = {
            sender: {
                id: User?.id
            },
            receiverGroup: selectedGroup?.id,
            // @ts-ignore
            receiverType: 'GROUP_CHAT',
            messageContent: message,
            sentAt: new Date().toISOString(),
            isRead: false
        };

        const updatedConversation = [...conversation];
        updatedConversation.unshift(newMessageObj);
        setConversation(updatedConversation);
        sendMessage(`/app/chat`, newMessageObj);
        setNewMessage("");
    };

    const firstMessage = messages.length === 0 && "👋😊";

    return (
        <>
            <div>
                <button className="flex relative items-center justify-between">
                    <GroupAvatar
                        id={selectedGroup?.id}
                        size="lg"
                        row
                    />
                </button>
            </div>
            <hr className="border-t border-gray-200 dark:border-gray-700 my-4 md:my-8" />
            <div className="dark:text-white md:max-h-[80vh] h-[65vh] scrollbar-hide overflow-y-auto flex flex-col-reverse">
                {messages.length === 0 && (
                    <div className="flex items-center justify-center h-full">
                        <button
                            className="text-6xl"
                            onClick={(e) => handleSendMessage(e, firstMessage)}
                        >
                            {firstMessage}
                        </button>
                    </div>
                )}
                {messages.map((message, index) => (
                    <div key={index}>
                        <div className="flex whitespace-break flex-col space-y-1 text-sm">
                            <p
                                className={`${message?.sender?.id === User?.id
                                    ? "w-fit max-w-[250px] md:max-w-[400px] text-right ml-auto text-white bg-purple-500 break-words"
                                    : "text-left bg-black dark:bg-gray-800 text-white w-fit max-w-[250px] md:max-w-[400px] break-words"
                                    } p-3 rounded-xl rounded-bl-none`}
                            >
                                {message?.messageContent}
                            </p>
                            <span
                                className={`${message?.sender?.id === User?.id
                                    ? "text-right ml-auto w-fit max-w-[400px]"
                                    : "w-fit max-w-[400px] text-left"
                                    } mb-2 dark:text-purple-300 text-xs`}
                            >
                                {multiFormatDateString(message?.sentAt) + " by " + message?.sender?.username}
                            </span>
                        </div>
                    </div>
                ))}
            </div>
            <form
                onSubmit={(e) => handleSendMessage(e, newMessage)}
                className="flex self-end mt-auto bottom-2 left-0 w-full px-2 items-center space-x-2"
            >
                <div className="w-full mt-1">
                    <TextInput
                        type="text"
                        value={newMessage}
                        onChange={(e) => setNewMessage(e.target.value)}
                        label=""
                        placeholder="Write your message here..."
                    />
                </div>
                <Button
                    type="submit"
                    size="xl"
                    color="yellowFit"
                    label=""
                    icon={<SendHorizontal color="#FFF" />}
                />
            </form>
        </>
    );
};

export default GroupChatBox;
