# Intern node update protocol

## 1. Some children initialization

 1. Path resolve
 2. Create root
 3. Create children (loop):
   1. Register parent
   2. Retrieve data from parent
   3. Run update into parent
		
## 2. Data reload from disk

 1. Root is reloading
 2. Children updates (loop):
   1. (one per parent) Clear children
   2. Retrieve data from parent
   3. Run update into parent