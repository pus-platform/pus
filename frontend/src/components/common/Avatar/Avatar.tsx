import { FC, ReactNode } from "react";
import { FaRegPlusSquare } from "react-icons/fa";
import { useNavigate } from "react-router-dom";
import { useGetUserById } from "@/lib/react-query/queries/user";
import Loader from "../Loader/Loader";

interface AvatarProps {
  id: number;
  imgSrc?: string;
  size?: "xs" | "sm" | "lg" | "xl" | "2xl";
  hasName?: boolean;
  hasUsername?: boolean;
  col?: boolean;
  border?: boolean;
  row?: boolean;
  start?: boolean;
  onChat?: boolean;
  isPost?: boolean;
  className?: string;
  onClick?: (e?) => void;
}

const Avatar: FC<AvatarProps> = ({
  imgSrc = "src/assets/icons/profile-placeholder.svg",
  id,
  size = "lg",
  className = "",
  hasName = true,
  hasUsername = true,
  border = false,
  row = true,
  start = false,
  col = false,
  onChat,
  isPost = true,
  onClick = () => { },
}) => {
  const { data: user, isPending: isUserPending } = useGetUserById({ id });
  const navigate = useNavigate();
  const baseSizeClasses = {
    xs: "min-h-[30px] w-[30px] h-[30px] md:w-[40px] md:h-[40px] min-w-[30px]",
    sm: "min-h-[52px] w-[52px] h-[52px] min-w-[52px]",
    lg: "max-h-[64px] w-[50px] h-[50px] lg:w-[64px] lg:h-[64px] md:w-[54px] md:h-[54px] max-w-[64px]",
    xl: "min-h-[100px] w-[100px] h-[100px] min-w-[100px]",
    "2xl": "min-h-[150px] w-[150px] h-[150px] min-w-[150px]",
  };

  if (isUserPending) {
    return (
      <Loader />
    )
  }

  const handelClick = () => {
    if (onClick)
      onClick();
    if (!onChat) {
      navigate(`/user-profile/${user?.id}`);
    }
  };
  const sizeClass = baseSizeClasses[size];

  const initials = user?.fullname?.split(" ").map((n) => n[0]).join("");
  const textSize =
    size === "xs" ? 12 :
      size === "sm" ? 18 :
        size === "lg" ? 28 :
          size === "xl" ? 40 :
            64;
  return (
    <button
      onClick={handelClick}
      className={`w-full ${row
        ? `flex gap-4 text-left  ${start ? "" : "items-center"}`
        : col
          ? "flex flex-col items-center justify-center text-center"
          : ""
        }` + className}
    >
      <div className={`relative ${sizeClass} flex items-center justify-center ${border ? "border-[3px] border-purple-500 rounded-full p-1" : ""}`} >
        <img src={imgSrc} alt={`${initials}`} className={`w-full h-full flex items-center justify-center text-white font-semibold text-[${textSize}px] bg-[#877EFF] rounded-full`} />
      </div>
      <div className={`pace-y-2 ${row ? "flex flex-col text-left" : ""}`}>
        {hasName && user?.fullname && (
          <span
            className={`hidden lg:inline-block text-black dark:text-white font-bold whitespace-nowrap ${size === "xs"
              ? "text-base font-semibold"
              : size === "2xl"
                ? "text-[28px] lg:text-[38px]"
                : "text-base lg:text-[18px]"
              }`}
          >
            {user?.fullname}
          </span>
        )}
        {hasUsername && user?.username && (
          <div className="max-w-24 ">
            <span
              className={` mt-1 ml-.5 ${size === "lg"
                ? `text-[12px] text-gray-400 dark:text-white ${!row && "mx-auto"
                } font-semibold`
                : size === "2xl"
                  ? "text-base lg:text-[18px] hidden lg:inline text-purple-300 -m-1"
                  : "text-[14px] hidden lg:inline text-purple-300 -m-1"
                }`}
            >
              {!!isPost ? `${user?.username}` : `@${user?.username}`}
            </span>
          </div>
        )}
      </div>
    </button>
  );
};

export default Avatar;
