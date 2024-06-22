import Avatar from "../Avatar/Avatar";
import { useUserContext } from "@/context/AuthContext";

interface MyAvatarProps {
  id?: number;
  hasName?: boolean;
  hasUsername?: boolean;
  isPost?: boolean;
  col?: boolean;
  row?: boolean;
  size?: "xs" | "sm" | "lg" | "xl" | "2xl";
  start?: boolean;
  className?: string;
  isGroup?: boolean;
  onClick?: (e?) => void;
}

const MyAvatar: React.FC<MyAvatarProps> = ({
  id,
  col = false,
  row = true,
  isPost = false,
  hasUsername = true,
  hasName = true,
  size,
  start = false,
  className = "",
  isGroup = false,
  onClick = () => { },
}) => {
  const user = useUserContext().user;

  return (
    <Avatar
      id={id ? id : user.id}
      row={row}
      isGroup={isGroup}
      hasName={hasName}
      hasUsername={hasUsername}
      size={size}
      col={col}
      start={start}
      isPost={isPost}
      className={className}
      onClick={onClick}
    />
  );
};

export default MyAvatar;
