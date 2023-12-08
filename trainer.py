# Paramater setting for trainer
trainer = Trainer(net=model,
                  dataset=BratsDataset,  
                  criterion=BCEDiceLoss(),  # Loss function class
                  lr=5e-4,
                  accumulation_steps=4,
                  batch_size=4,
                  fold=0,
                  num_epochs=50,
                  path_to_csv=config.path_to_csv)
