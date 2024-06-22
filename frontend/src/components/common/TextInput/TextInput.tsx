import { FC } from "react";

interface InputProps {
  type: string;
  label?: string;
  placeholder?: string;
  value?: string;
  name?: string;
  onChange?: (event: React.ChangeEvent<HTMLInputElement>) => void;
  autoFocus?: any;
}

const CommentInput: FC<InputProps> = ({
  type,
  label,
  placeholder,
  value,
  name,
  onChange,
  autoFocus,
}) => {
  return (
    <div className="md:mb-2 mb-1">
      {label && (
        <label className="block text-sm font-medium text-black dark:text-white py-2 md:py-4">
          {label}
        </label>
      )}
      <div className="relative">
        <input
          type={type}
          autoFocus={autoFocus}
          placeholder={placeholder}
          value={value}
          name={name}
          onChange={onChange}
          className="md:mt-1 mt-.5 block w-full py-2 dark:border dark:border-gray-800 border rounded-lg shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 dark:text-white text-black dark:bg-gray-900 placeholder:text-gray-600 px-4 border-purple-200"
        />
      </div>
    </div>
  );
};

export default CommentInput;
