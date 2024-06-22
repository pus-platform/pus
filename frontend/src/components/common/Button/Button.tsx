import { FC, ReactNode } from "react";
import {
  Button as FlowButton,
  CustomFlowbiteTheme,
  ButtonProps as FlowbiteButtonProps,
} from "flowbite-react";

interface ButtonProps extends FlowbiteButtonProps {
  icon?: ReactNode;
}

const Button: FC<ButtonProps> = ({ icon, ...props }) => {
  const ButtonTheme: CustomFlowbiteTheme["button"] = {
    base: "group w-fit font-semibold flex whitespace-nowrap	 items-stretch items-center rounded-lg text-center capitalize  font-medium  relative focus:border-0 focus:z-10 focus:outline-none",
    fullSized: "w-full",
    color: {
      purple: `bg-purple-500 dark:bg-purple-500 hover:bg-purple-600 transition w-full text-[28px]`,
      purpleFit: `bg-purple-500 hover:bg-purple-600 transition dark:bg-purple-500 text-white h-fit w-fit`,
      black: `bg-black d dark:bg-white capitalize text-white dark:text-black w-full`,
      blackFit: `bg-black d dark:bg-white capitalize text-white dark:text-black h-fit w-fit`,
      yellow: `bg-yellow-500 dark:yellow-500 hover:bg-yellow-600 transition w-full`,
      red: `bg-red-500 dark:red-500 w-full dark:text-white text-center flex justify-center py-2 font-bold`,
      yellowFit: `bg-yellow-500 dark:yellow-500 rounded h-fit w-fit rounded-xl`,
      transparent: `border border-purple-500 text-black dark:text-white w-full`,
      transparentFit: `h-fit w-fit`,
    },
    inner: {
      base: "flex items-stretch items-center transition-all duration-200 gap-2",
    },
    label:
      "ml-2 inline-flex h-4 w-4 items-center justify-center rounded-full bg-cyan-200 text-xs font-semibold text-cyan-800",
    outline: {
      color: {
        gray: "border border-gray-900 dark:border-white",
        default: "border-0",
        light: "",
      },
      off: "",
      on: "flex justify-center bg-white text-gray-900 transition-all duration-75 ease-in group-enabled:group-hover:bg-opacity-0 group-enabled:group-hover:text-inherit dark:bg-gray-900 dark:text-white w-full",
      pill: {
        off: "rounded-lg",
        on: "rounded-full",
      },
    },
    pill: {
      off: "rounded-lg",
      on: "rounded-full",
    },
    size: {
      xs: "p-0",
      md: "text-sm px-4 py-2 mx-auto",
      lg: "text-base px-4 py-2 mx-auto text-white",
      xl: "text-lg px-3 py-3",
    },
  };

  return (
    <FlowButton theme={ButtonTheme} {...props} className="group gap-3">
      {icon}
      {props.label}
    </FlowButton>
  );
};

export default Button;
