import { useState } from 'react';
import { ChevronsUpDown, Check } from 'lucide-react';
import { Command, CommandEmpty, CommandGroup, CommandInput, CommandItem } from '@/components/ui';
import { Popover, PopoverTrigger, PopoverContent } from '@/components/ui/popover';
import { CommandList } from 'cmdk';

const Combobox = ({ options, value, onChange, formType }) => {
    const [open, setOpen] = useState(false);

    const handleSelect = (option) => {
        onChange(option.value);
        setOpen(false);
    };

    return (
        <Popover open={open} onOpenChange={setOpen}>
            <PopoverTrigger className='px-8 py-4 rounded-md' asChild>
                <button
                    type="button"
                    className="shad-input flex justify-between items-center w-full h-fit overflow-hidden"
                    aria-haspopup="listbox"
                    aria-expanded={open}
                >
                    {value ? options.find((option) => option.value === value)?.label : `Select a ${formType}`}
                    <ChevronsUpDown className="ml-2 max-h-5 max-w-5 min-h-5 min-w-5" />
                </button>
            </PopoverTrigger>
            <PopoverContent className="w-full p-0 bg-dark-2">
                <Command>
                    <CommandList>
                        <CommandInput placeholder="Search views..." />
                        <CommandEmpty> {`${formType} not valid`} </CommandEmpty>
                        <CommandGroup
                            className="max-h-60 overflow-y-auto"
                        >
                            {options.map((option) => (
                                <CommandItem
                                    key={option.value}
                                    onSelect={() => handleSelect(option)}
                                    className="cursor-pointer hover:bg-dark-4"
                                >
                                    {option.label}
                                    {value === option.value && <Check className="ml-auto h-5 w-5" />}
                                </CommandItem>
                            ))}
                        </CommandGroup>
                    </CommandList>
                </Command>
            </PopoverContent>
        </Popover>
    );
};

export default Combobox;
