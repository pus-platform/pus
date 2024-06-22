import Loader from "../Loader/Loader";
import { useGetGroupChat } from "@/lib/react-query/queries/groupChat";

interface AvatarProps {
    id: number;
    size?: "xs" | "sm" | "lg" | "xl" | "2xl";
    hasName?: boolean;
    col?: boolean;
    border?: boolean;
    row?: boolean;
    start?: boolean;
    className?: string;
}

const GroupAvatar: React.FC<AvatarProps> = ({
    id,
    size = "lg",
    className = "",
    hasName = true,
    border = false,
    row = true,
    start = false,
    col = false,
}) => {
    const { data: group, isPending: isGroupPending } = useGetGroupChat({ id });
    const baseSizeClasses = {
        xs: "min-h-[30px] w-[30px] h-[30px] md:w-[40px] md:h-[40px] min-w-[30px]",
        sm: "min-h-[52px] w-[52px] h-[52px] min-w-[52px]",
        lg: "max-h-[64px] w-[50px] h-[50px] lg:w-[64px] lg:h-[64px] md:w-[54px] md:h-[54px] max-w-[64px]",
        xl: "min-h-[100px] w-[100px] h-[100px] min-w-[100px]",
        "2xl": "min-h-[150px] w-[150px] h-[150px] min-w-[150px]",
    };

    if (isGroupPending) {
        return <Loader />
    }

    const sizeClass = baseSizeClasses[size];

    const initials = group?.name?.split(" ").map((n) => n[0]).join("");
    const textSize =
        size === "xs" ? 12 :
            size === "sm" ? 18 :
                size === "lg" ? 22 :
                    size === "xl" ? 36 : 60;
    return (
        <div className={`w-full ${row ? `flex gap-4 text-left  ${start ? "" : "items-center"}` : col ? "flex flex-col items-center justify-center text-center" : ""}` + className}>
            <div className={`relative ${sizeClass} flex items-center justify-center ${border ? "border-[3px] border-purple-500 rounded-full p-1" : ""}`} >
                <img src={"src/assets/icons/group-chat.svg"} alt={`${initials}`} className={`w-full h-full flex items-center justify-center text-white font-semibold text-[${textSize}px] bg-[#877EFF] rounded-full`} />
            </div>
            <div className={`space-y-2 ${row ? "flex justify-center items-center flex-col text-left" : ""}`}>
                {
                    hasName && group?.name && (
                        <span
                            className={`hidden lg:inline-block text-black dark:text-white font-bold whitespace-nowrap ${size === "xs"
                                ? "text-base font-semibold"
                                : size === "2xl"
                                    ? "text-[28px] lg:text-[38px]"
                                    : "text-base lg:text-[18px]"
                                }`}
                        >
                            {group?.name}
                        </span>
                    )
                }
            </div>
        </div>
    );
};

export default GroupAvatar;
