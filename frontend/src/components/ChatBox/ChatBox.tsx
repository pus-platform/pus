import { useEffect, useState } from "react";
import Button from "../common/Button/Button";
import TextInput from "../common/TextInput/TextInput";
import { useUserContext } from "@/context/AuthContext";
import { Message } from "@/lib/types";
import { useGetMessagesByUserId } from "@/lib/react-query/queries/message";
import { multiFormatDateString } from "@/lib/utils";
import Loader from "../common/Loader/Loader";
import { useGetUserById } from "@/lib/react-query/queries/user";
import axios from "axios";
import Cookies from "js-cookie";
import { SendHorizontal } from "lucide-react";
import { useMessage } from "@/context/MessageContext";
import MyAvatar from "../common/MyAvatar/MyAvatar";

const ChatBox: React.FC<{ selectedUserId: number }> = ({ selectedUserId }) => {
  const { user: User, isLoading: isUserLoading } = useUserContext();
  const { data: selectedUser, isPending: isUserPending } = useGetUserById({ id: selectedUserId });
  const { data: initialConversation, isPending: isMessagePending } = useGetMessagesByUserId({ userId: selectedUser?.id, senderId: User?.id });
  const [newMessage, setNewMessage] = useState("");
  const [imageUrl, setImageUrl] = useState<string>("src/assets/icons/profile-placeholder.svg");
  const [conversation, setConversation] = useState([]);
  const { sendMessage } = useMessage();
  let messages = conversation || [];
  useEffect(() => {
    messages = conversation || [];
  }, [conversation, selectedUserId]);
  useEffect(() => {
    setConversation(!!initialConversation ? [...initialConversation] : []);
  }, [initialConversation]);

  useEffect(() => {
    if (selectedUser?.imageUrl) {
      fetchImageUrl(selectedUser.imageUrl);
    }
  }, [selectedUser?.imageUrl]);

  if (isMessagePending || isUserLoading || isUserPending)
    return (
      <Loader />
    )

  const handleSendMessage = (e: React.FormEvent, message: string) => {
    e.preventDefault();
    if (!selectedUser || !message) return;

    const newMessageObj: Message = {
      sender: {
        id: User?.id
      },
      receiverUser: {
        id: selectedUser?.id
      },
      // @ts-ignore
      receiverType: 'USER',
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

  const fetchImageUrl = async (url: string) => {
    try {
      const response = await axios.get(url, {
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${Cookies.get("token")}`,
        },
        responseType: "blob",
      });
      setImageUrl(URL.createObjectURL(response.data));
    } catch (error) {
      console.error("Error fetching image:", error.message);
    }
  };

  const firstMessage = messages.length === 0 && "👋😊";

  return (
    <>
      <div>
        <button className="flex relative items-center justify-between">
          <MyAvatar
            id={selectedUser?.id}
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
                {multiFormatDateString(message?.sentAt)}
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

export default ChatBox;
