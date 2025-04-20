import React from "react";
import { Plus, Edit, Trash2 } from "lucide-react";
import { z } from "zod";
import { useToast } from "../../hooks/use-toast";
import { Button } from "../../components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "../../components/ui/card";

// Define the schema for Tenant data
const propertyFormSchema = z.object({
  name: z.string().min(1, "Tenant name is required"),
  address: z.string().min(1, "Address is required"),
  city: z.string().min(1, "City is required"),
  postalCode: z.string().min(5, "Valid postal code is required"),
  units: z.string().transform(val => parseInt(val, 10)).refine(val => !isNaN(val) && val > 0, "Must be a valid number"),
  purchasePrice: z.string().optional().transform(val => val ? parseInt(val, 10) : undefined),
  currentValue: z.string().optional().transform(val => val ? parseInt(val, 10) : undefined),
});

type PropertyFormValues = z.infer<typeof propertyFormSchema>;

export default function TenantsList(props) {
  const { toast } = useToast();
  const isAddTenantDialogOpen = props.isAddTenantDialogOpen;

  const isLoading = props.isLoading;
  const properties = props.properties;
  const error = "";

  const addTenantComponent = React.Children.toArray(props.children).find(
    child => child.props['id'] === 'add-tenant'
  );

  /* Add Tenant mutation
  const addTenantMutation = useMutation({
    mutationFn: (data: TenantFormValues) => 
      apiRequest('POST', '/api/properties', data),
    onSuccess: () => {
      toast({
        title: "Success",
        description: "Tenant added successfully",
      });
      refetch();
      setIsAddTenantDialogOpen(false);
      form.reset();
    },
    onError: (error: Error) => {
      toast({
        title: "Error",
        description: `Failed to add Tenant: ${error.message}`,
        variant: "destructive",
      });
    }
  });*/

  if (isLoading) {
    return (
      <Card>
        <CardHeader>
          <CardTitle>Tenants</CardTitle>
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
          <CardTitle>Tenants</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="bg-red-50 p-4 rounded-md">
            <p className="text-red-800">Failed to load tenants. Please try again later.</p>
          </div>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card>
      <CardHeader className="flex flex-row items-center justify-between">
        <CardTitle>Tenants</CardTitle>

        <Button size="sm" onClick={props.onChangeAddTenantDialogOpen}>
          <Plus className="h-4 w-4 mr-2" />
          Add Tenant
        </Button>

        {isAddTenantDialogOpen && (
          addTenantComponent
        )}
      </CardHeader>
      <CardContent>
        {properties && properties.length > 0 ? (
          <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
            {properties.map((tenant: any) => (
              <Card key={tenant.id} className="overflow-hidden">
                <div className="p-4">
                  <div className="flex justify-between items-start">
                    <div>
                      <h3 className="font-semibold text-lg">{tenant.name}</h3>
                      <p className="text-sm text-gray-500 mt-1">{tenant.lastname}</p>
                      <p className="text-sm text-gray-500">{tenant.email}, {tenant.phone}</p>
                    </div>
                    <div className="flex space-x-2">
                      <Button variant="ghost" size="icon" onClick={props.addNewTenant}>
                        <Plus className="h-4 w-4" />
                      </Button>
                      <Button variant="ghost" size="icon">
                        <Edit className="h-4 w-4" />
                      </Button>
                      <Button variant="ghost" size="icon">
                        <Trash2 className="h-4 w-4" />
                      </Button>
                    </div>
                  </div>
                </div>
              </Card>
            ))}
          </div>
        ) : (
          <div className="text-center p-8">
            <p className="text-gray-500">No tenants found. Add your first tenant to get started.</p>
            <Button 
              className="mt-4"
              onClick={props.onChangeAddTenantDialogOpen}
            >
              <Plus className="h-4 w-4 mr-2" />
              Add Your First Tenant
            </Button>
          </div>
        )}
      </CardContent>
    </Card>
  );
}
