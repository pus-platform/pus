import { useParams } from "react-router-dom";
import ChatBox from "../../components/ChatBox/ChatBox";

const SingleChat = () => {
  const { selectedUserId } = useParams();
  return (
    <div className="max-w-sm w-full px-2 h-screen relative">
      <ChatBox selectedUserId={parseInt(selectedUserId)} />
    </div>
  );
};

export default SingleChat;
