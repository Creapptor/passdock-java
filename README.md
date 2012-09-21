passdock-java
=============

PassDock Java sample code.

### Dependancies

This sample code requires [Apache's HTTPComponents HttpClient](http://hc.apache.org/).

### Get your authentication token

You need to register on [api.passdock.com](https://api.passdock.com) and get an [api token](https://api.passdock.com/settings).

### Let's start

Once obtained you can create a new istance of the Passdock object.

    Passdock passdock = new Passdock("YOUR_TOKEN");

Then you can call some methods to get the desired informations out of Passdock or to create new Passes.

Return values are either JSON formatted String or boolean values. To handle JSON you can use any Java JSON library you like, like Google's gson or Jackson. Get them at [jsor.org](http://json.org/).

#### Get Templates

Get all your templates for your account

	String templates = passdock.getTemplates();
    
#### Get a Template

Pass the id of the template as argument

    	String template = passdock.getTemplate(82)

#### Destroy a Template 

The first argument is the template id, the second one if you want exanded errors.

    	boolean deletedOrNot = passdock.destroyTemplate(82, true);

#### Get a Pass

The first parameter is the Pass ID, the second its template ID

    String pass = passdock.getPass(132, 82);

#### Create a Pass

The first argument is a JSON string representing the Pass structure, more informations on [api.passdock.com/doc](https://api.passdock.com/doc).
The second argument is the template id, the third one if you want debug informations and the last one exanded errors.

    boolean createdOrNot = passdock.createPass(pass, 82, true, false);

#### Update a Pass

The first argument is a JSON string representing the update to apply to the pass, more informations on [api.passdock.com/doc](https://api.passdock.com/doc).
The second argument is the template id, the third one if you want debug informations and the last one exanded errors. It will return a JSON representation of the updated pass.

    String updatedPass = passdock.updatePass(update , 94, 82, true, false);

#### Destroy a Pass 

The first argument is the template id, the second one if you want exanded errors.

    boolean destroyedOrNot = passdock.destroyPass(94, 82, true);