// @ts-nocheck
import Heading from "../../components/Heading/Heading";
import { MessageCircle, Bell, Heart, MessageSquare, MessageSquareHeart, MessageSquareMore, MessageSquareReply, UserRoundPlus, MessageCircleReply } from 'lucide-react';
import MyAvatar from "../../components/common/MyAvatar/MyAvatar";
import { useGetNotifications } from "@/lib/react-query/queries/notification";
import { multiFormatDateString } from "@/lib/utils";
import { NotificationType } from "@/lib/enums";
import Loader from "@/components/common/Loader/Loader";


const Notifications: React.FC = () => {
  const { data: notifications, isPending } = useGetNotifications();

  function getIconForNotificationType(notificationType: NotificationType) {
    switch (notificationType) {
      case NotificationType[NotificationType.FOLLOW_REQUEST]:
        return <UserRoundPlus className="text-purple-500 size-9" />;
      case NotificationType[NotificationType.MESSAGE]:
      case NotificationType[NotificationType.GROUP_MESSAGE]:
        return <MessageCircle color="#877EFF" className="size-9" />;
      case NotificationType[NotificationType.STORY_REPLY]:
        return <MessageCircleReply className="text-purple-500 size-9" />;
      case NotificationType[NotificationType.COMMENT]:
        return <MessageSquareMore className="text-purple-500 size-9" />;
      case NotificationType[NotificationType.COMMENT_REPLY]:
        return <MessageSquareReply className="text-purple-500 size-9" />;
      case NotificationType[NotificationType.COMMENT_LIKE]:
        return <MessageSquareHeart className="text-purple-500 size-9" />;
      case NotificationType[NotificationType.POST_LIKE]:
      case NotificationType[NotificationType.STORY_LIKE]:
        return <Heart className="text-purple-500 size-9" />;
      default:
        return null;
    }
  }

  if (isPending) {
    return (
      <Loader />
    )
  }

  return (
    <div className="dark:text-white h-full w-full px-2 md:px-8 pb-40">
      <Heading
        icon={<Bell className="h-8 w-8" />}
        label="Notifications"
        // isFilter={true}
      />
      <div className="md:p-6 space-y-4 w-full">
        {notifications?.map((notification) => (
          <div
            key={notification?.id}
            className="flex items-center px-0 md:px-4 py-4 gap-[40px] border-b border-[#1F1F22]"
          >
            {getIconForNotificationType(notification?.notificationType)}
            <div className="flex items-center justify-start w-full">
              <div>
                <MyAvatar id={notification?.user.id} hasUsername={false} hasName={false} size="lg" />
              </div>
              <div className="flex flex-col gap-[4px] justify-center ml-1">
                <p className="text-[18px] font-[450] dark:text-white">
                  {notification?.content}
                </p>
                <p className="text-[14px] font-[400] text-[#7878A3] dark:text-[#bebee6]">
                  {multiFormatDateString(notification.notifiedAt)}
                </p>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default Notifications;
