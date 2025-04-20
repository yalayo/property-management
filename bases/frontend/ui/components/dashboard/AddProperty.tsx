import React from "react";
import { useState } from "react";
import { Plus, Edit, Trash2 } from "lucide-react";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { useToast } from "../../hooks/use-toast";
import { Button } from "../ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "../ui/card";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from "../ui/dialog";
import { Input } from "../ui/input";
import { Label } from "../ui/label";
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from "../ui/form";

// Define the schema for property data
const propertyFormSchema = z.object({
  name: z.string().min(1, "Property name is required"),
  address: z.string().min(1, "Address is required"),
  city: z.string().min(1, "City is required"),
  postalCode: z.string().min(5, "Valid postal code is required"),
  units: z.string().transform(val => parseInt(val, 10)).refine(val => !isNaN(val) && val > 0, "Must be a valid number"),
  purchasePrice: z.string().optional().transform(val => val ? parseInt(val, 10) : undefined),
  currentValue: z.string().optional().transform(val => val ? parseInt(val, 10) : undefined),
});

type PropertyFormValues = z.infer<typeof propertyFormSchema>;

export default function AddProperty(props) {
  const { toast } = useToast();

  const isLoading = props.isLoading;
  const error = "";

  // Form setup
  const form = useForm<PropertyFormValues>({
    resolver: null, //zodResolver(propertyFormSchema),
    defaultValues: {
      name: "",
      address: "",
      city: "",
      postalCode: "",
      units: "1",
      purchasePrice: "",
      currentValue: "",
    },
  });

  if (isLoading) {
    return (
      <Card>
        <CardHeader>
          <CardTitle>Properties</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="flex justify-center p-4">
            <div className="animate-spin w-8 h-8 border-4 border-primary-600 border-t-transparent rounded-full" />
          </div>
        </CardContent>
      </Card>
    );
  }

  if (error) {
    return (
      <Card>
        <CardHeader>
          <CardTitle>Properties</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="bg-red-50 p-4 rounded-md">
            <p className="text-red-800">Failed to load properties. Please try again later.</p>
          </div>
        </CardContent>
      </Card>
    );
  }

  return (
    <Dialog open={true} onOpenChange={props.onChangeAddPropertyDialogClose}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Add New Property</DialogTitle>
        </DialogHeader>
        <Form {...form}>
          <div className="space-y-4">
            <FormField
              name="name"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Property Name</FormLabel>
                  <FormControl>
                    <Input  placeholder="E.g., Riverside Apartment"
                            defaultValue={props.propertyName} 
                            onBlur={props.onChangePropertyName} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              name="address"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Address</FormLabel>
                  <FormControl>
                    <Input  placeholder="Street address"
                            defaultValue={props.propertyAddress} 
                            onBlur={props.onChangePropertyAddress} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <div className="grid grid-cols-2 gap-4">
              <FormField
                name="city"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>City</FormLabel>
                    <FormControl>
                      <Input  placeholder="City"
                              defaultValue={props.propertyCity} 
                              onBlur={props.onChangePropertyCity} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                name="postalCode"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Postal Code</FormLabel>
                    <FormControl>
                      <Input  placeholder="Postal code"
                              defaultValue={props.propertyPostalCode} 
                              onBlur={props.onChangePropertyPostalCode} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
            </div>
            <FormField
              name="units"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Number of Units</FormLabel>
                  <FormControl>
                    <Input  type="number" min="1"
                            defaultValue={props.propertyUnits} 
                            onBlur={props.onChangePropertyUnits} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <div className="grid grid-cols-2 gap-4">
              <FormField
                name="purchasePrice"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Purchase Price (€)</FormLabel>
                    <FormControl>
                      <Input  type="number" placeholder="Optional"
                              defaultValue={props.propertyPurchasePrice} 
                              onBlur={props.onChangePropertyPurchasePrice} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                name="currentValue"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Current Value (€)</FormLabel>
                    <FormControl>
                      <Input  type="number" placeholder="Optional"
                              defaultValue={props.propertyCurrentValue} 
                              onBlur={props.onChangePropertyCurrentValue} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
            </div>
            <div className="flex justify-end space-x-4 pt-4">
              <Button 
                type="button" 
                variant="outline"
                onClick={props.onChangeAddPropertyDialogClose}
              >
                Cancel
              </Button>
              <Button disabled={false} onClick={props.submitProperty}>
                {false ? 'Saving...' : 'Save Property'}
              </Button>
            </div>
          </div>
        </Form>
      </DialogContent>
    </Dialog>
  );
}
