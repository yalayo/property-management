import React from "react";
import { CheckCircle } from "lucide-react";
import { z } from "zod";
import { useToast } from "../../hooks/use-toast";
import { Button } from "../../components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "../../components/ui/card";


export default function SelectTenant(props) {
  const { toast } = useToast();

  const isLoading = props.isLoading;
  const tenants = props.tenants;
  const error = "";

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
        <CardTitle>Select tenant</CardTitle>
      </CardHeader>
      <CardContent>
        {tenants && tenants.length > 0 ? (
          <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
            {tenants.map((tenant: any) => (
              <Card key={tenant.id}
                    onClick={() => props.onSelectTenant(tenant.id)} 
                    className={`overflow-hidden cursor-pointer ${
                      props.selectedTenant === tenant.id 
                        ? 'border-2 border-blue-500 shadow-lg' 
                        : 'opacity-50 hover:opacity-100'}`}>
                <div className="p-4">
                  <div className="flex justify-between items-start">
                    <div>
                      <h3 className="font-semibold text-lg">{tenant.name}</h3>
                      <p className="text-sm text-gray-500 mt-1">{tenant.lastname}</p>
                      <p className="text-sm text-gray-500">{tenant.email} {tenant.phone}</p>
                    </div>
                    {props.selectedTenant === tenant.id && (
                      <div className="text-green-500">
                        <CheckCircle className="h-5 w-5" />
                      </div>
                    )}
                  </div>
                </div>
              </Card>
            ))}
          </div>
        ) : (
          <div className="text-center p-8">
            <p className="text-gray-500">No tenants found.</p>
          </div>
        )}
      </CardContent>

      <div className="p-4 border-t">
        <Button
          variant="outline"
          className="flex items-center justify-center"
          onClick={props.onSaveSelection}
        >
          Continue
        </Button>
      </div>
    </Card>
  );
}
