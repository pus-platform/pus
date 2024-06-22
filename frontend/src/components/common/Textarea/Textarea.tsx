import React, { FC, useRef, useEffect } from "react";

interface TextareaProps {
  label?: string;
  placeholder?: string;
  value?: string;
  name?: string;
  row?: number;
  onChange?: (e: React.ChangeEvent<HTMLTextAreaElement>) => void;
  setIsTextarea?: (isTextarea: boolean) => void;
}

const Textarea: FC<TextareaProps> = ({
  label,
  placeholder,
  value,
  name,
  row = 4,
  onChange,
  setIsTextarea,
}) => {
  const textareaRef = useRef<HTMLTextAreaElement>(null);

  const handleClickOutside = (event: MouseEvent) => {
    if (
      textareaRef.current &&
      !textareaRef.current.contains(event.target as Node) &&
      !value
    ) {
      setIsTextarea && setIsTextarea(false);
    }
  };

  useEffect(() => {
    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, []);

  return (
    <div className="mb-2">
      {label && (
        <label className="block text-sm font-medium text-black dark:text-white py-4">
          {label}
        </label>
      )}
      <textarea
        ref={textareaRef}
        placeholder={placeholder}
        value={value}
        name={name}
        onChange={onChange}
        className="mt-1 block w-full py-2 dark:border-0 border rounded-lg shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 dark:text-white text-black dark:bg-gray-650 placeholder:text-gray-600 px-4 border-purple-200"
        rows={row}
      />
    </div>
  );
};

export default Textarea;
