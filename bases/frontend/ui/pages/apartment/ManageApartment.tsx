import React from "react";
import { useEffect, useRef, useState } from 'react';
import {  } from "react";
import { z } from 'zod';
import { useToast } from "../../hooks/use-toast";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "../../components/ui/card";
import { Button } from "../../components/ui/button";
import { Input } from "../../components/ui/input";
import { 
  UploadCloud, 
  FileSpreadsheet, 
  FileText, 
  Check, 
  AlertTriangle, 
  Loader2, 
  Eye, 
  Clock,
  CircleX,
  FileIcon
} from "lucide-react";

import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from "../../components/ui/accordion";

import { Badge } from "../../components/ui/badge";

export default function ManageApartment(props) {
  const { toast } = useToast();

  const inputRef = useRef(null);

  useEffect(() => {
    if (props.editSurface && inputRef.current) {
      inputRef.current.focus();
    } else if (props.editAccountability && inputRef.current) {
      inputRef.current.focus();
    } else if (props.editTax && inputRef.current) {
      inputRef.current.focus();
    } else if (props.editGarbage && inputRef.current) {
      inputRef.current.focus();
    } else if (props.editRainwater && inputRef.current) {
      inputRef.current.focus();
    } else if (props.editWastewater && inputRef.current) {
      inputRef.current.focus();
    } else if (props.editDrinkingwater && inputRef.current) {
      inputRef.current.focus();
    }
    
  }, [props.editSurface, props.editAccountability, props.editTax, props.editGarbage, props.editRainwater, props.editWastewater, props.editDrinkingwater]);

  const amountSchema = z.string()
    .nonempty("This field is required")
    .refine(val => !isNaN(parseFloat(val)), {
      message: "Must be a valid number",
    });

  const [surfaceError, setSurfaceError] = useState('');
  const [accountabilityError, setAccountabilityError] = useState('');
  const [taxError, setTaxError] = useState('');
  const [garbageError, setGarbageError] = useState('');

  const [rainwater, setValueRainWater] = useState(props.rainwater || '');
  const [rainWaterError, setRainWaterError] = useState(''); 
  
  const [wastewater, setValueWastewater] = useState(props.wastewater || '');
  const [wastewaterError, setWastewaterError] = useState('');

  const [drinkingwater, setValueDrinkingwater] = useState(props.drinkingwater || '');
  const [drinkingwaterError, setDrinkingwaterError] = useState('');

  const [startDateError, setStartDateError] = useState('');

  const formatDate = (dateStr) => {
    if (!dateStr) return 'Add';
    const date = new Date(dateStr);
    return date.toLocaleDateString(); // Customize format if needed
  };
  
  return (
    <Card>
      <CardHeader>
        <CardTitle>Apartment - {props.selectedApartment}</CardTitle>
      </CardHeader>
      <CardContent>
        <div className="mt-8">
          <h3 className="text-lg font-medium mb-4">Manage apartment data</h3>
          
          <div className="space-y-3">
            <div key="surface" className="transition-colors hover:bg-gray-100 rounded p-2">
              <div className="flex items-center justify-between w-full">
                <div className="flex w-full">
                  <p className="font-medium">Wohnflasche</p>
                </div>

                {props.editSurface ? (
                  <div className="relative mt-2">
                    <Input
                      className="w-[200px] h-8 text-right text-sm"
                      placeholder="Apartment surface"
                      ref={inputRef}
                      defaultValue={props.surface}
                      onKeyDown={(e) => {
                        if (e.key === 'Enter') {
                          const inputVal = e.target.value;
                    
                          const result = amountSchema.safeParse(inputVal);
                          if (!result.success) {
                            setSurfaceError(result.error.errors[0].message);
                          } else {
                            setSurfaceError('');
                            props.onChangePropertySurface(inputVal);
                          }
                        } else if (e.key === 'Escape') {
                          props.cancelEditSurface(false);
                        }
                      }}
                    />
                    {surfaceError && (
                      <p className="text-red-500 text-xs mt-1 text-right">
                        {surfaceError}
                      </p>)}
                  </div>
                ) : (
                  <a
                    onClick={props.onEditSurface}
                    className="text-blue-600 underline cursor-pointer whitespace-nowrap h-8 flex items-center text-sm"
                  >
                    {props.surface ? `${props.surface} m2` : 'Add'}
                  </a>
                )}  
              </div>
            </div>

            <div key="startDate" className="transition-colors hover:bg-gray-100 rounded p-2">
              <div className="flex items-center justify-between w-full">
                <div className="flex w-full">
                  <p className="font-medium">Beginndatum</p>
                </div>

                {props.editStartDate ? (
                  <div className="relative mt-2">
                    <input
                      type="date"
                      className="w-[200px] h-8 text-right text-sm"
                      ref={inputRef}
                      defaultValue={props.startDate}
                      onChange={(e) => {
                        const value = e.target.value;
                        if (!value) {
                          setStartDateError('Invalid date');
                        } else {
                          setStartDateError('');
                          setStartDate(value);
                          props.onChangeStartDate(value);
                        }
                      }}
                      onKeyDown={(e) => {
                        if (e.key === 'Escape') {
                          props.cancelEditStartDate(false);
                        }
                      }}
                    />
                    {startDateError && (
                      <p className="text-red-500 text-xs mt-1 text-right">{startDateError}</p>
                    )}
                  </div>
                ) : (
                  <a
                    onClick={props.onEditStartDate}
                    className="text-blue-600 underline cursor-pointer whitespace-nowrap h-8 flex items-center text-sm"
                  >
                    {props.startDate ? formatDate(props.startDate) : 'Add'}
                  </a>
                )}  
              </div>
            </div>

            <div key="property-tax" className="transition-colors hover:bg-gray-100 rounded p-2">
              <div className="flex items-center justify-between w-full">
                <div className="flex w-full">
                  <p className="font-medium">Endedatum</p>
                </div>

                {props.editTax ? (
                  <div className="relative mt-2">
                    <Input
                      className="w-[200px] h-8 text-right text-sm"
                      placeholder="Property tax paid"
                      ref={inputRef}
                      defaultValue={props.tax}
                      onKeyDown={(e) => {
                        if (e.key === 'Enter') {
                          const inputVal = e.target.value;
                    
                          const result = amountSchema.safeParse(inputVal);
                          if (!result.success) {
                            setTaxError(result.error.errors[0].message);
                          } else {
                            setTaxError('');
                            props.onChangePropertyTax(inputVal);
                          }
                        } else if (e.key === 'Escape') {
                          props.cancelEditTax(false);
                        }
                      }}
                    />
                    {taxError && (
                      <p className="text-red-500 text-xs mt-1 text-right">
                        {taxError}
                      </p>)}
                  </div>
                ) : (
                  <a
                    onClick={props.onEditTax}
                    className="text-blue-600 underline cursor-pointer whitespace-nowrap h-8 flex items-center text-sm"
                  >
                    {props.tax ? `€ ${props.tax}` : 'Add'}
                  </a>
                )}  
              </div>
            </div>

            <div key="garbage" className="transition-colors hover:bg-gray-100 rounded p-2">
              <div className="flex items-center justify-between w-full">
                <div className="flex w-full">
                  <p className="font-medium">Müllabfuhr</p>
                </div>

                {props.editGarbage ? (
                  <div className="relative mt-2">
                    <Input
                      className="w-[200px] h-8 text-right text-sm"
                      placeholder="Garbage service paid"
                      ref={inputRef}
                      defaultValue={props.garbage}
                      onKeyDown={(e) => {
                        if (e.key === 'Enter') {
                          const inputVal = e.target.value;
                    
                          const result = amountSchema.safeParse(inputVal);
                          if (!result.success) {
                            setGarbageError(result.error.errors[0].message);
                          } else {
                            setGarbageError('');
                            props.onChangePropertyGarbage(inputVal);
                          }
                        } else if (e.key === 'Escape') {
                          props.cancelEditGarbage(false);
                        }
                      }}
                    />
                    {garbageError && (
                      <p className="text-red-500 text-xs mt-1 text-right">
                        {garbageError}
                      </p>)}
                  </div>
                ) : (
                  <a
                    onClick={props.onEditGarbage}
                    className="text-blue-600 underline cursor-pointer whitespace-nowrap h-8 flex items-center text-sm"
                  >
                    {props.garbage ? `€ ${props.garbage}` : 'Add'}
                  </a>
                )}  
              </div>
            </div>

            <div key="rain-water" className="transition-colors hover:bg-gray-100 rounded p-2">
              <div className="flex items-center justify-between w-full">
                <div className="flex w-full">
                  <p className="font-medium">Niederschlagwasser</p>
                </div>

                {props.editRainwater ? (
                  <div className="relative mt-2">
                    <Input
                      className="w-[200px] h-8 text-right text-sm"
                      placeholder="Rain water paid"
                      ref={inputRef}
                      defaultValue={props.rainwater}
                      onKeyDown={(e) => {
                        if (e.key === 'Enter') {
                          const inputVal = e.target.value;
                    
                          const result = amountSchema.safeParse(inputVal);
                          if (!result.success) {
                            setRainWaterError(result.error.errors[0].message);
                          } else {
                            setRainWaterError('');
                            props.onChangePropertyRainwater(inputVal);
                          }
                        } else if (e.key === 'Escape') {
                          props.cancelEditRainwater(false);
                        }
                      }}
                    />
                    {rainWaterError && (
                      <p className="text-red-500 text-xs mt-1 text-right">
                        {rainWaterError}
                      </p>)}
                  </div>
                ) : (
                  <a
                    onClick={props.onEditRainwater}
                    className="text-blue-600 underline cursor-pointer whitespace-nowrap h-8 flex items-center text-sm"
                  >
                    {props.rainwater ? `€ ${props.rainwater}` : 'Add'}
                  </a>
                )}  
              </div>
            </div>

            <div key="wastewater" className="transition-colors hover:bg-gray-100 rounded p-2">
              <div className="flex items-center justify-between w-full">
                <div className="flex w-full">
                  <p className="font-medium">Schmutzwasser</p>
                </div>

                {props.editWastewater ? (
                  <div className="relative mt-2">
                    <Input
                      className="w-[200px] h-8 text-right text-sm"
                      placeholder="Waste water paid"
                      ref={inputRef}
                      defaultValue={props.wastewater}
                      onKeyDown={(e) => {
                        if (e.key === 'Enter') {
                          const inputVal = e.target.value;
                    
                          const result = amountSchema.safeParse(inputVal);
                          if (!result.success) {
                            setWastewaterError(result.error.errors[0].message);
                          } else {
                            setWastewaterError('');
                            props.onChangePropertyWastewater(inputVal);
                          }
                        } else if (e.key === 'Escape') {
                          props.cancelEditWastewater(false);
                        }
                      }}
                    />
                    {wastewaterError && (
                      <p className="text-red-500 text-xs mt-1 text-right">
                        {wastewaterError}
                      </p>)}
                  </div>
                ) : (
                  <a
                    onClick={props.onEditWastewater}
                    className="text-blue-600 underline cursor-pointer whitespace-nowrap h-8 flex items-center text-sm"
                  >
                    {props.wastewater ? `€ ${props.wastewater}` : 'Add'}
                  </a>
                )}  
              </div>
            </div>

            <div key="drinkingwater" className="transition-colors hover:bg-gray-100 rounded p-2">
              <div className="flex items-center justify-between w-full">
                <div className="flex w-full">
                  <p className="font-medium">Trinkwasser</p>
                </div>

                {props.editDrinkingwater ? (
                  <div className="relative mt-2">
                    <Input
                      className="w-[200px] h-8 text-right text-sm"
                      placeholder="Drink water paid"
                      ref={inputRef}
                      defaultValue={props.drinkingwater}
                      onKeyDown={(e) => {
                        if (e.key === 'Enter') {
                          const inputVal = e.target.value;
                    
                          const result = amountSchema.safeParse(inputVal);
                          if (!result.success) {
                            setDrinkingwaterError(result.error.errors[0].message);
                          } else {
                            setDrinkingwaterError('');
                            props.onChangePropertyDrinkingwater(inputVal);
                          }
                        } else if (e.key === 'Escape') {
                          props.cancelEditDrinkingwater(false);
                        }
                      }}
                    />
                    {drinkingwaterError && (
                      <p className="text-red-500 text-xs mt-1 text-right">
                        {drinkingwaterError}
                      </p>)}
                  </div>  
                ) : (
                  <a
                    onClick={props.onEditDrinkingwater}
                    className="text-blue-600 underline cursor-pointer whitespace-nowrap h-8 flex items-center text-sm"
                  >
                    {props.drinkingwater ? `€ ${props.drinkingwater}` : 'Add'}
                  </a>
                )}  
              </div>
            </div>
          </div>
        </div>
      </CardContent>

      <div className="p-4 border-t flex gap-2">
        <Button
          variant="outline"
          className="flex items-center justify-center"
          onClick={props.onSaveSelection}
        >
          Continue
        </Button>

        <Button
          variant="outline"
          className="flex items-center justify-center"
          onClick={props.onCancel}
        >
          Cancel
        </Button>
      </div>
    </Card>
  );
}
