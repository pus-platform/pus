import { MessageCircle } from 'lucide-react';
import Heading from "../../components/Heading/Heading";
import { useNavigate } from "react-router-dom";
import { useState } from "react";
import { useMediaQuery } from "@mui/material";
import { useGetFollowers, useGetFollowing } from "@/lib/react-query/queries/follower";
import { useUserContext } from "@/context/AuthContext";
import Loader from "@/components/common/Loader/Loader";
import ChatBox from '@/components/ChatBox/ChatBox';
import MyAvatar from '@/components/common/MyAvatar/MyAvatar';
import { useGetGroupChats } from '@/lib/react-query/queries/groupChat';
import GroupAvatar from '@/components/common/Avatar/GroupAvatar';
import GroupChatBox from '@/components/ChatBox/GroupChatBox';

const Chats = () => {
  const [selectedUser, setSelectedUser] = useState<number>();
  const [selectedGroup, setSelectedGroup] = useState<number>();

  const { user: curUser } = useUserContext();
  const navigate = useNavigate();
  const isMobile = useMediaQuery("(max-width:600px)");
  const { data: following, isPending: isFollowingPending } = useGetFollowing({ followerId: curUser?.id });
  const { data: followers, isPending: isFollowersPending } = useGetFollowers({ followedId: curUser?.id });
  const { data: groups, isPending: isGroupsPending } = useGetGroupChats();

  if (isFollowingPending || isFollowersPending || isGroupsPending)
    return <Loader />

  const users = following?.map((follow) => follow?.followed)
    .concat(followers?.map((follow) => follow?.follower))
    .reduce((acc, current) => {
      const x = acc.find(item => item.id === current?.id);
      if (!x) {
        return acc.concat([current]);
      } else {
        return acc;
      }
    }, []);

  const handleUserSelect = (id: number) => {
    if (!isMobile) {
      setSelectedUser(id);
      setSelectedGroup(undefined);
    } else {
      navigate(`/chats/${id}`);
    }
  };

  const handleGroupSelect = (id: number) => {
    if (!isMobile) {
      setSelectedGroup(id);
      setSelectedUser(undefined);
    } else {
      navigate(`/chats/group/${id}`);
    }
  };

  return (
    <div className="dark:text-white h-full w-full px-2 md:px-8 pb-40">
      <Heading icon={<MessageCircle className="h-8 w-8" />} label="All Chats" />
      <div className="grid grid-cols-1 gap-4 lg:grid-cols-4 md:grid-cols-5">
        <div className="lg:col-span-1 col-span-2 space-y-4 md:space-y-5 scrollbar-hide overflow-y-scroll max-h-screen ">
          <div className="max-h-[74vh] flex flex-col">
            {users?.map((friend) => (
              <div
                key={friend?.id}
                className="flex items-center justify-between w-full py-[12px] md:py-[30px] border-b dark:border-[#1F1F22] border-gray-700"
                onClick={() => handleUserSelect(friend?.id)}
              >
                <MyAvatar
                  id={friend?.id}
                  className="pointer-events-none"
                  size="lg"
                  row
                  onClick={(e) => {
                    e.preventDefault();
                    handleUserSelect(friend?.id)
                  }}
                />
              </div>
            ))
            }{
              groups?.map((group) => (
                <div
                  key={group?.id}
                  className="flex items-center justify-between w-full py-[12px] md:py-[30px] border-b dark:border-[#1F1F22] border-gray-700"
                  onClick={() => handleGroupSelect(group?.id)}
                >
                  <GroupAvatar
                    id={group?.id}
                    className="pointer-events-none hover:cursor-pointer"
                    size="lg"
                    row
                  />
                </div>
              ))
            }
          </div>
        </div>
        <div className="hidden relative md:block col-span-3 mt-3 px-2 lg:px-6 rounded-xl dark:bg-black border border-gray-200 dark:border-gray-700 w-full p-3 lg:p-6 max-w-[900px] mx-auto">
          {selectedUser && <ChatBox selectedUserId={selectedUser} />}
          {selectedGroup && <GroupChatBox selectedGroupId={selectedGroup} />}
        </div>
      </div>
    </div>
  );
};

export default Chats;
