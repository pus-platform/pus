import { View } from "@/lib/enums";

interface SelectProps {
  label?: string;
  options?: any;
  name?: string;
  value?: View;
  onChange?: (e: React.ChangeEvent<HTMLSelectElement>) => void;
  className?: string;
}

const Select: React.FC<SelectProps> = ({ label, options, name, value, onChange, className }) => {
  return (
    <div className={"mb-2" + className}>
      {label && (
        <label className="block text-sm font-medium text-black dark:text-white py-4">
          {label}
        </label>
      )}
      <select
        name={name}
        defaultValue={"Choose a View"}
        value={value}
        onChange={onChange}
        className="mt-1 block w-full py-2 dark:border-0 border rounded-lg shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 dark:text-white text-black bg-gray-100 dark:bg-gray-650 placeholder:text-gray-600 px-4 border-purple-200"
      >
        <option>Who can see your post?</option>
        {options.map((option: any) => (
          <option key={option.value} value={option.value}>{option.name}</option>
        ))}
      </select>
    </div>
  );
};

export default Select;
